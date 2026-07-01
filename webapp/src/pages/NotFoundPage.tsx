import {Link} from 'react-router-dom'

export function NotFoundPage() {
    return <main className="center-screen"><h1>404</h1><p>Страница не найдена.</p><Link className="button primary"
                                                                                        to="/">На главную</Link></main>
}
