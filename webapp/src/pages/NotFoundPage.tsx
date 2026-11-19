import {ErrorPage} from './ErrorPage'

export function NotFoundPage() {
    return <ErrorPage status={404} standalone={false}/>
}
