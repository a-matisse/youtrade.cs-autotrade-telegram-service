// Local-only contract fixture for docs/external-patches/lis-tm/7/web-auth-frontend.md.
// The local Telegram flow issues synthetic credentials and opens the normal frontend callback.
const http = require('http');
const crypto = require('crypto');
const {readFileSync} = require('node:fs');
const {resolve} = require('node:path');
const sessions = new Map();
let calls = [];
let refreshMode = 'ok';
let profileMode = 'legacy';
let profileReads = 0;
let operationMode = 'initial';
const previewAssetsPath = '/api/web/v1/overview/preview-assets/';
const skins = resolve(__dirname, '../../../webapp/src/assets/skins/hero');
const previewAssets = new Map([
  ['operation-ak.png', ['image/png', readFileSync(resolve(skins, 'ak/ak-47-asiimov.png'))]],
  ['operation-awp.png', ['image/png', readFileSync(resolve(skins, 'awp/awp-redline.png'))]],
  ['avatar.svg', ['image/svg+xml', '<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40"><rect width="40" height="40" fill="#b8323a"/><circle cx="20" cy="15" r="6" fill="white"/></svg>']],
]);
const itemNames = ['AWP | Redline', 'AK-47 | Asiimov', 'USP-S | Cortex', 'M4A1-S | Cyrex', 'Glock-18 | Vogue',
  'AK-47 | Slate', 'AWP | Atheris', 'M4A4 | Neo-Noir', 'P250 | See Ya Later', 'MP9 | Starlight Protector',
  'Desert Eagle | Ocean Drive', 'FAMAS | Roll Cage', 'MAC-10 | Disco Tech', 'SSG 08 | Fever Dream',
  'AWP | Redline', 'AK-47 | Asiimov'];
const operations = itemNames.map((itemName, index) => ({id: `fixture-operation-${index}`,
  type: index % 2 ? 'SELL' : 'BUY', itemName,
  imageUrl: index % 5 === 4 ? null : `${previewAssetsPath}operation-${index % 2 ? 'ak' : 'awp'}.png`,
  market: index % 2 ? 'MARKET_CSGO' : 'CSFLOAT', amount: 42.25 + index, currency: 'USD',
  occurredAt: new Date(Date.UTC(2026, 10, 20, 12, -index)).toISOString()}));
const profile = () => ({id: 42, login: null,
  nickname: profileMode === 'nickname' ? 'alex' : profileMode === 'legacy' ? 'Пользователь' : 'Alex',
  firstName: null, lastName: null, avatar: {
    source: ['uploaded', 'renew', 'expiry'].includes(profileMode) ? 'UPLOADED' : ['avatar', 'broken'].includes(profileMode) ? 'TELEGRAM' : 'DEFAULT',
    url: profileMode === 'avatar' ? `${previewAssetsPath}avatar.svg`
      : profileMode === 'broken' ? `${previewAssetsPath}missing-avatar.png?attempt=${profileReads}`
      : ['uploaded', 'renew', 'expiry'].includes(profileMode)
        ? `/api/web/v1/user/profile/avatar/file/fixture-id?signature=${profileMode === 'renew' && profileReads === 1 ? 'expired' : 'valid'}&generation=${profileReads}` : null,
    uploadedImageAvailable: ['uploaded', 'renew', 'expiry'].includes(profileMode), imageId: 'fixture-id',
    width: 1, height: 1, contentType: 'image/png', byteSize: 68,
    urlExpiresAt: profileMode === 'expiry' && profileReads === 1 ? new Date(Date.now() + 30_100).toISOString()
      : ['uploaded', 'renew', 'expiry'].includes(profileMode) ? new Date(Date.now() + 900_000).toISOString() : null,
  }, version: 0, profileInitializedAt: '2026-11-20T10:00:00Z', createdAt: '2026-11-20T10:00:00Z', updatedAt: '2026-11-20T10:00:00Z'});
const cookie = '__Secure-ycs-refresh';
const send = (res, status, data) => {res.writeHead(status, {'Content-Type': 'application/json', 'Cache-Control': 'no-store'}); res.end(data === undefined ? '' : JSON.stringify(data));};
const portfolioPath = '/api/web/v1/user/portfolio';
const performancePath = '/api/web/v1/user/performance';
const walletPath = '/api/web/v1/user/service-wallet';
const portfolioPaths = new Set([portfolioPath, performancePath, walletPath, portfolioPath + '/positions', portfolioPath + '/history']);
const capital = {totalCapitalUsd: 240, buyAvailableUsd: 80, buyPendingUsd: 20, saleCashEstimatedUsd: 40,
  inventoryEstimatedUsd: 100, oldestCapitalCalculatedAt: 1795334400, partial: false, positionCount: 2,
  valuedPositionCount: 2, matchedOpenCostUsd: 90, estimatedNetProceedsUsd: 100, estimatedPnlUsd: 10, estimatedReturnPercent: 11.11};
const portfolio = {tdpId: 5, name: 'Тестовый портфель', source: 'CSFLOAT', destination: 'MARKET_CSGO', capital,
  sharePercent: 100, activeBargainCount: 1, offeredAmountUsd: 12, marketBalances: [{market: 'CSFLOAT', side: 'BUY',
    accountId: 7, availableUsd: 80, pendingUsd: 20, observedAt: 1795334400, observedCurrency: 'USD'},
  {market: 'MARKET_CSGO', side: 'SELL', accountId: 8, availableUsd: 35, pendingUsd: 5,
    observedAt: 1795334400, observedCurrency: 'USD'}]};
const position = {positionId: 11, tdpId: 5, itemName: 'AK-47 | Slate', boughtOn: 'CSFLOAT', destination: 'MARKET_CSGO',
  purchasePriceUsd: 45, purchaseCostUsd: 45, purchasedAt: 1795334400, unlockAt: 1795939200, held: true,
  listingStatus: 'NOT_LISTED', strategyMinPriceUsd: null, strategyMaxPriceUsd: null, listingPriceUsd: null,
  marketEstimateUsd: 64, estimateCollectedAt: 1795334400, estimatedNetProceedsUsd: 60, estimatedPnlUsd: 15,
  estimatedReturnPercent: 33.33};
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
  if (path === '/__fixture__/reset') {sessions.clear(); calls = []; refreshMode = 'ok'; profileMode = 'legacy'; profileReads = 0; operationMode = 'initial'; return send(res, 204);}
  if (path === '/__fixture__/operations') {operationMode = url.searchParams.get('value'); return send(res, 204);}
  if (path === '/__fixture__/profile') {profileMode = url.searchParams.get('value'); profileReads = 0; return send(res, 204);}
  if (path === '/__fixture__/mode') {refreshMode = url.searchParams.get('value'); return send(res, 204);}
  if (path === '/__fixture__/expire') {for (const session of sessions.values()) session.access = 'expired'; return send(res, 204);}
  calls.push({method: req.method, path}); // No headers, OAuth codes or credentials in the journal.
  if (path.startsWith(previewAssetsPath)) {
    const asset = previewAssets.get(path.slice(previewAssetsPath.length));
    if (!asset) return send(res, 404, {});
    res.writeHead(200, {'Content-Type': asset[0]}); return res.end(asset[1]);
  }
  const refresh = req.headers.cookie?.split(';').map(value => value.trim()).find(value => value.startsWith(cookie + '='))?.slice(cookie.length + 1);
  const session = [...sessions.values()].find(value => !value.revoked && value.refresh === refresh);
  const authorized = [...sessions.values()].find(value => !value.revoked && req.headers.authorization === 'Bearer ' + value.access);
  if (path === '/api/web/v1/auth/telegram' && req.method === 'GET') {
    const session = issue(); setCookie(res, session.refresh);
    res.writeHead(302, {Location: '/auth/callback'}); return res.end();
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
  if (path === '/api/web/v1/user/profile' && req.method === 'GET') {
    if (!authorized) return send(res, 401, {});
    profileReads++;
    if (profileMode === 'missing') return send(res, 404, {});
    if (profileMode === 'unavailable') return send(res, 503, {});
    if (profileMode === 'blocked') return send(res, 403, {});
    return send(res, 200, profile());
  }
  if (path === '/api/web/v1/user/profile/avatar/file/fixture-id') {
    if (url.searchParams.get('signature') !== 'valid') return send(res, 403, {});
    res.writeHead(200, {'Content-Type': 'image/png', 'Cache-Control': 'no-store'});
    return res.end(Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wl6GHcAAAAASUVORK5CYII=', 'base64'));
  }
  if (portfolioPaths.has(path) && req.method === 'GET') {
    if (!authorized) return send(res, 401, {});
    const tdpId = url.searchParams.has('tdpId') ? Number(url.searchParams.get('tdpId')) : null;
    if (tdpId !== null && tdpId !== portfolio.tdpId) return send(res, 404, {});
    if (path === walletPath) return send(res, 200, {product: 'YOUTRADE_PRO', serviceBalanceUsd: 42.15});
    if (path === portfolioPath) return send(res, 200, {revision: 1, calculatedAt: 1795334400,
      valuationBasis: 'fixture', capital, portfolios: [portfolio]});
    if (path === performancePath) return send(res, 200, {fromSeconds: 1792742400, untilSeconds: 1795334400,
      calculatedAt: 1795334400, tdpId, feeBasis: 'YCS_COMPLETED_SALE_V1', incomeUsd: 7.2, incomePercent: 6.315789,
      saleVolumeUsd: 114, matchedCostUsd: 101.1, netProceedsUsd: 108.3, tradeCount: 1, unmatchedCount: 0});
    if (path === portfolioPath + '/positions') {
      const afterId = Number(url.searchParams.get('afterId'));
      const status = url.searchParams.get('status');
      if (status && status !== position.listingStatus) return send(res, 200, {positions: [], nextAfterId: null});
      return send(res, 200, afterId === position.positionId
        ? {positions: [{...position, positionId: 12, itemName: 'AWP | Redline', marketEstimateUsd: 43,
          estimatedNetProceedsUsd: 40, estimatedPnlUsd: -5, estimatedReturnPercent: -11.11}], nextAfterId: null}
        : {positions: [position], nextAfterId: position.positionId});
    }
    const fromSeconds = Number(url.searchParams.get('from_seconds'));
    const untilSeconds = Number(url.searchParams.get('until_seconds'));
    if (!fromSeconds || fromSeconds >= untilSeconds) return send(res, 400, {});
    const duration = untilSeconds - fromSeconds;
    const step = 300;
    const start = Math.ceil((fromSeconds + duration * .08) / step) * step;
    const count = Math.floor(duration * .68 / step);
    const historyPoint = (snapshotAt, revision, totalCapitalUsd) => ({snapshotAt, observedAt: snapshotAt, revision,
      resolution: 'FIVE_MINUTES', valuationBasis: 'fixture', capital: {...capital, totalCapitalUsd}});
    const points = Array.from({length: count}, (_, index) => {
      const progress = index / count;
      const value = 208 + progress * 27 + Math.sin(progress * 19) * 5;
      return historyPoint(start + index * step, index, Number(value.toFixed(2)));
    });
    const latest = Math.floor((fromSeconds + duration * .9) / step) * step;
    if (latest >= fromSeconds && latest < untilSeconds) points.push(historyPoint(latest, count, capital.totalCapitalUsd));
    return send(res, 200, {fromSeconds, untilSeconds, tdpId, fiveMinuteFrom: fromSeconds, points});
  }
  if (path === '/api/web/v1/overview/documents') return send(res, 200, [{type: 'YCS_SERVICE_TERMS',
    version: 'fixture-1', contentHash: 'fixture-hash', published: true, locale: 'ru', content: '# Fixture terms'}]);
  if (path === '/api/web/v1/user/p2p/document-acceptances') {
    if (!authorized) return send(res, 401, {});
    if (req.method === 'GET') return send(res, 200, {data: []});
    if (req.method === 'POST') return send(res, 200, {data: {type: 'YCS_SERVICE_TERMS',
      version: 'fixture-1', contentHash: 'fixture-hash', id: 1, channel: 'WEB', acceptedAt: new Date().toISOString()}});
  }
  if (path === '/api/web/v1/overview') return send(res, 200, {statistics: {daily: []}, pricing: {}});
  if (path === '/api/web/v1/steam/currency') return send(res, 200, [{currency: 'RUB', rate: 80.25, time: 0, source: 'fixture'}]);
  if (path === '/api/web/v1/overview/deals') return send(res, 200, []);
  if (path === '/api/web/v1/overview/operations') {
    if (operationMode === 'unavailable') return send(res, 503, {});
    const etag = `"fixture-operations-${operationMode}"`;
    const headers = {'Content-Type': 'application/json', 'Cache-Control': 'no-cache, public', ETag: etag};
    if (req.headers['if-none-match'] === etag) {res.writeHead(304, headers); return res.end();}
    const rows = operationMode === 'updated' ? [{...operations[0], id: 'fixture-operation-new',
      itemName: 'New operation', occurredAt: '2026-11-20T12:01:00Z',
      imageUrl: `${previewAssetsPath}missing.png`}, ...operations] : operations;
    res.writeHead(200, headers);
    return res.end(JSON.stringify({operations: operationMode === 'empty' ? [] : rows, asOf: '2026-11-20T12:02:00Z'}));
  }
  return send(res, 501, {message: 'Outside the modeled contract'});
}).listen(process.env.PORT);
