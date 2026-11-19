// Local-only contract fixture for docs/external-patches/lis-tm/7/web-auth-frontend.md.
// The fake Telegram approval endpoint is not a simulation of Telegram's cryptography.
const http = require('http');
const crypto = require('crypto');
const sessions = new Map();
let calls = [];
let refreshMode = 'ok';
const cookie = '__Secure-ycs-refresh';
const send = (res, status, data) => {res.writeHead(status, {'Content-Type': 'application/json', 'Cache-Control': 'no-store'}); res.end(data === undefined ? '' : JSON.stringify(data));};
const setCookie = (res, value) => res.setHeader('Set-Cookie', `${cookie}=${value}; HttpOnly; Secure; SameSite=Strict; Path=/api/web/v1/auth; Max-Age=${value ? 3600 : 0}`);
const issue = () => {
  const id = crypto.randomUUID();
  const session = {refresh: crypto.randomUUID(), access: 'fixture.' + crypto.randomUUID() + '.token', revoked: false};
  sessions.set(id, session); return session;
};
http.createServer(async (req, res) => {
  const url = new URL(req.url, 'http://localhost');
  const path = url.pathname;
  if (path === '/__fixture__/state') return send(res, 200, {calls});
  if (path === '/__fixture__/reset') {sessions.clear(); calls = []; refreshMode = 'ok'; return send(res, 204);}
  if (path === '/__fixture__/mode') {refreshMode = url.searchParams.get('value'); return send(res, 204);}
  if (path === '/__fixture__/expire') {for (const session of sessions.values()) session.access = 'expired'; return send(res, 204);}
  if (path === '/__fixture__/telegram') {
    const session = issue(); setCookie(res, session.refresh);
    res.writeHead(302, {Location: '/auth/callback'}); return res.end();
  }
  calls.push({method: req.method, path}); // No headers, OAuth codes or credentials in the journal.
  const refresh = req.headers.cookie?.split(';').map(value => value.trim()).find(value => value.startsWith(cookie + '='))?.slice(cookie.length + 1);
  const session = [...sessions.values()].find(value => !value.revoked && value.refresh === refresh);
  const authorized = [...sessions.values()].find(value => !value.revoked && req.headers.authorization === 'Bearer ' + value.access);
  if (path === '/api/web/v1/auth/telegram' && req.method === 'GET') {
    res.writeHead(302, {Location: '/__fixture__/telegram'}); return res.end();
  }
  if (path === '/api/web/v1/auth/refresh' && req.method === 'POST') {
    if (!session) return send(res, 401, {message: 'Invalid session'});
    if (!req.headers.origin) return send(res, 403, {message: 'Missing Origin'});
    if (refreshMode === 'unavailable') return send(res, 503, {message: 'Unavailable'});
    if (refreshMode === 'blocked') return send(res, 403, {message: 'Forbidden'});
    session.refresh = crypto.randomUUID(); session.access = 'fixture.' + crypto.randomUUID() + '.token';
    setCookie(res, session.refresh);
    if (refreshMode === 'lost-response') {req.socket.destroy(); return;}
    return send(res, 200, {accessToken: session.access, tokenType: 'Bearer', expiresIn: 3600});
  }
  if (path === '/api/web/v1/auth/logout' && req.method === 'POST') {
    if (!authorized) return send(res, 401, {message: 'Invalid session'});
    authorized.revoked = true; setCookie(res, ''); return send(res, 204);
  }
  if (path === '/api/web/v1/auth/me' && req.method === 'GET')
    return authorized ? send(res, 200, {id: 42, telegramId: 777, botLinked: false}) : send(res, 401, {});
  if (path === '/api/web/v1/overview/documents') return send(res, 200, [{type: 'YCS_SERVICE_TERMS',
    version: 'fixture-1', contentHash: 'fixture-hash', published: true, locale: 'ru', content: '# Fixture terms'}]);
  if (path === '/api/web/v1/user/p2p/document-acceptances') {
    if (!authorized) return send(res, 401, {});
    if (req.method === 'GET') return send(res, 200, {data: []});
    if (req.method === 'POST') return send(res, 200, {data: {type: 'YCS_SERVICE_TERMS',
      version: 'fixture-1', contentHash: 'fixture-hash', id: 1, channel: 'WEB', acceptedAt: new Date().toISOString()}});
  }
  if (path === '/api/web/v1/overview') return send(res, 200, {statistics: {daily: []}, pricing: {}});
  if (path === '/api/web/v1/overview/deals' || path === '/api/web/v1/steam/currency') return send(res, 200, []);
  return send(res, 501, {message: 'Outside the modeled contract'});
}).listen(process.env.PORT);
