import {Fragment, useEffect, useState} from 'react'
import {Link} from 'react-router-dom'
import {landingApi} from '../api/services'
import type {LandingDocument} from '../api/types'
import logo from '../assets/brand/youtrade-mark-transparent.png'
import '../landing.css'

const documentNames: Record<string, string> = {
  CLIENT_EXCHANGE_TERMS: 'Условия обмена для клиента',
  OPERATOR_EXCHANGE_TERMS: 'Условия работы оператора',
  YCS_SERVICE_TERMS: 'Условия использования Y.CS',
}

function InlineText({text}: {text: string}) {
  const parts = text.split(/(\*\*[^*]+\*\*|https?:\/\/\S+)/g).filter(Boolean)
  return <>{parts.map((part, index) => {
    if (part.startsWith('**') && part.endsWith('**')) return <strong key={index}>{part.slice(2, -2)}</strong>
    if (part.startsWith('http')) return <a key={index} href={part} target="_blank" rel="noreferrer">{part}</a>
    return <Fragment key={index}>{part}</Fragment>
  })}</>
}

function DocumentText({content}: {content: string}) {
  return <div className="document-content">{content.split(/\r?\n/).map((line, index) => {
    const heading = line.match(/^#\s+(.+)/)
    const clause = line.match(/^(\d+)\.\s+(.+)/)
    if (heading) return <h1 key={index}><InlineText text={heading[1]}/></h1>
    if (clause) return <div className="document-clause" key={index}><span>{clause[1]}</span><p><InlineText text={clause[2]}/></p></div>
    if (!line.trim()) return <div className="document-space" key={index}/>
    return <p key={index}><InlineText text={line}/></p>
  })}</div>
}

export function OffersPage() {
  const [documents, setDocuments] = useState<LandingDocument[]>([])
  const [selectedType, setSelectedType] = useState<string | null>(null)
  const [error, setError] = useState(false)

  useEffect(() => {
    void landingApi.documents().then(items => {
      setDocuments(items)
      setSelectedType(items.find(item => item.published)?.type ?? items[0]?.type ?? null)
    }).catch(() => setError(true))
  }, [])

  const selected = documents.find(item => item.type === selectedType) ?? documents[0]
  return <main className="offers-page">
    <header className="offers-header"><Link className="landing-brand footer-brand" to="/"><img src={logo} alt=""/><span>Y.CS</span></Link><div><span>Документы</span><b>Оферты и условия</b></div><Link className="offers-back" to="/">← На главную</Link></header>
    <div className="offers-layout">
      <aside className="documents-nav"><div className="documents-nav-head"><span>Доступные документы</span><b>{documents.length.toString().padStart(2, '0')}</b></div>{documents.map(document => <button type="button" className={document.type === selected?.type ? 'is-selected' : ''} key={`${document.type}-${document.version}`} onClick={() => setSelectedType(document.type)}><span>{documentNames[document.type] ?? document.type}</span><small>Версия {document.version} · {document.published ? 'Опубликовано' : 'Проект'}</small></button>)}{!documents.length && !error && <div className="documents-state">Загрузка документов…</div>}{error && <div className="documents-state is-error">Не удалось загрузить документы</div>}</aside>
      <article className="document-view">{selected ? <><div className="document-meta"><div><span>{selected.published ? 'Опубликованный документ' : 'Проект документа'}</span><b>{documentNames[selected.type] ?? selected.type}</b></div><dl><div><dt>Версия</dt><dd>{selected.version}</dd></div><div><dt>Язык</dt><dd>{selected.locale.toUpperCase()}</dd></div></dl></div><DocumentText content={selected.content}/></> : !error && <div className="document-empty">Выберите документ</div>}</article>
    </div>
  </main>
}
