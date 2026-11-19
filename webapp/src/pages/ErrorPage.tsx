import logo from '../assets/brand/youtrade-mark-transparent.png'
import './ErrorPage.css'

export function ErrorPage({status}: {status: number}) {
  const serverError = status >= 500
  const message = status === 404
    ? {title: 'Страница не найдена.', description: 'Возможно, ссылка устарела или в адресе опечатка. Вернитесь на главную — всё основное там.'}
    : status === 403 || status === 401
      ? {title: 'Доступ ограничен.', description: 'Эта страница недоступна для просмотра. Можно вернуться на главную или обратиться в поддержку.'}
      : status === 429
        ? {title: 'Слишком много запросов.', description: 'Подождите немного и попробуйте открыть страницу ещё раз.'}
        : serverError
          ? {title: 'Сервис временно недоступен.', description: 'Не удалось открыть страницу. Попробуйте обновить её чуть позже или напишите в поддержку.'}
          : {title: 'Не удалось открыть страницу.', description: 'Попробуйте вернуться на главную. Если ошибка повторяется, напишите в поддержку.'}

  return <main className="error-page">
    <header className="error-page-header">
      <a className="error-page-brand" href="/" aria-label="Y.CS — на главную"><img src={logo} alt=""/><span>Y.CS</span></a>
      <a className="error-page-support" href="https://t.me/youtradecs_sup" target="_blank" rel="noreferrer">Поддержка</a>
    </header>
    <section className="error-page-content" aria-labelledby="error-title">
      <div className="error-page-copy">
        <span className="error-page-label">Ошибка {status}</span>
        <h1 id="error-title">{message.title}</h1>
        <p>{message.description}</p>
        <div className="error-page-actions">
          <a className="error-page-button" href={serverError || status === 429 ? '' : '/'}>{serverError || status === 429 ? 'Обновить страницу' : 'На главную'}</a>
          <a className="error-page-button is-secondary" href={serverError || status === 429 ? '/' : 'https://t.me/youtradecs_sup'}>{serverError || status === 429 ? 'На главную' : 'Написать в поддержку'}</a>
        </div>
      </div>
      <div className="error-page-code" aria-hidden="true">{status}</div>
    </section>
    <footer className="error-page-footer"><span>Автоматизация торговли предметами CS2.</span><a href="https://docs.youtradecs.xyz">Документация</a></footer>
  </main>
}
