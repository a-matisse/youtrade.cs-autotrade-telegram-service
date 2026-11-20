import logo from '../assets/brand/youtrade-mark-transparent.png'
import './ErrorPage.css'
import {SiteLink} from '../components/layout/SiteLink'

export function ErrorPage({status, standalone = true}: {status: number; standalone?: boolean}) {
  const serverError = status >= 500
  const PageLink = standalone ? 'a' : SiteLink
  const message = status === 404
    ? {title: 'Страница не найдена.', description: 'Возможно, ссылка устарела или в адресе опечатка. Вернитесь на главную — всё основное там.'}
    : status === 403 || status === 401
      ? {title: 'Доступ ограничен.', description: 'Эта страница недоступна для просмотра. Можно вернуться на главную или обратиться в поддержку.'}
      : status === 429
        ? {title: 'Слишком много запросов.', description: 'Подождите немного и попробуйте открыть страницу ещё раз.'}
        : serverError
          ? {title: 'Сервис временно недоступен.', description: 'Не удалось открыть страницу. Попробуйте обновить её чуть позже или напишите в поддержку.'}
          : {title: 'Не удалось открыть страницу.', description: 'Попробуйте вернуться на главную. Если ошибка повторяется, напишите в поддержку.'}

  return <main className={`error-page${standalone ? ' site-control-surface--dark' : ' error-page-in-site'}`}>
    {standalone && <header className="error-page-header">
      <a className="error-page-brand" href="/" aria-label="Y.CS — на главную"><img src={logo} alt=""/><span>Y.CS</span></a>
      <a className="site-control site-control--quiet site-control--large error-page-support" href="https://t.me/youtradecs_sup" target="_blank" rel="noreferrer">Поддержка</a>
    </header>}
    <section className="error-page-content" aria-labelledby="error-title">
      <div className="error-page-copy">
        <span className="error-page-label">Ошибка {status}</span>
        <h1 id="error-title">{message.title}</h1>
        <p>{message.description}</p>
        <div className="error-page-actions">
          <PageLink className="site-control site-control--accent site-control--large error-page-button" href={serverError || status === 429 ? '' : '/'}>{serverError || status === 429 ? 'Обновить страницу' : 'На главную'}</PageLink>
          <PageLink className="site-control site-control--quiet site-control--large error-page-button" href={serverError || status === 429 ? '/' : 'https://t.me/youtradecs_sup'}>{serverError || status === 429 ? 'На главную' : 'Написать в поддержку'}</PageLink>
        </div>
      </div>
      <div className="error-page-code" aria-hidden="true">{status}</div>
    </section>
    <footer className="error-page-footer"><span>Автоматизация торговли предметами CS2.</span><PageLink href="https://docs.youtradecs.xyz">Обучение</PageLink></footer>
  </main>
}
