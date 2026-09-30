import {Fragment, useEffect, useState} from 'react'
import {Link} from 'react-router-dom'
import {documentApi, landingApi, type DocumentAcceptance} from '../api/services'
import {useAuth} from '../auth/authState'
import type {LandingDocument} from '../api/types'
import logo from '../assets/brand/youtrade-mark-transparent.png'
import '../landing.css'
import '../offers-document.css'

const deferredDocumentTypes = new Set(['CLIENT_EXCHANGE_TERMS', 'OPERATOR_EXCHANGE_TERMS'])
const contractDocumentTypes = new Set(['YCS_SERVICE_TERMS', 'YCS_PAYMENT_TERMS', 'YCS_REFUND_POLICY'])

type DocumentLocale = 'ru' | 'en' | 'ka'
const locales: DocumentLocale[] = ['ru', 'en', 'ka']
const documentNames: Record<DocumentLocale, Record<string, string>> = {
  ru: {
    YCS_SERVICE_TERMS: 'Условия использования Y.CS',
    YCS_PAYMENT_TERMS: 'Условия оплаты услуг Y.CS',
    YCS_REFUND_POLICY: 'Правила возврата средств',
    YCS_PRIVACY_POLICY: 'Политика обработки персональных данных',
    YCS_REFERRAL_PARTNER_TERMS: 'Условия для реферальных партнёров',
  },
  en: {
    YCS_SERVICE_TERMS: 'Y.CS Terms of Service',
    YCS_PAYMENT_TERMS: 'Y.CS Payment Terms',
    YCS_REFUND_POLICY: 'Refund Policy',
    YCS_PRIVACY_POLICY: 'Privacy Policy',
    YCS_REFERRAL_PARTNER_TERMS: 'Referral Partner Terms',
  },
  ka: {
    YCS_SERVICE_TERMS: 'Y.CS-ის მომსახურების პირობები',
    YCS_PAYMENT_TERMS: 'Y.CS-ის გადახდის პირობები',
    YCS_REFUND_POLICY: 'თანხის დაბრუნების წესები',
    YCS_PRIVACY_POLICY: 'პერსონალურ მონაცემთა დაცვის პოლიტიკა',
    YCS_REFERRAL_PARTNER_TERMS: 'რეფერალური პარტნიორების პირობები',
  },
}
const labels = {
  ru: {documents: 'Документы', title: 'Оферты и условия', back: 'На главную', available: 'Доступные документы',
    version: 'Версия', published: 'Опубликовано', draft: 'Проект', publishedDocument: 'Опубликованный документ',
    draftDocument: 'Проект документа', language: 'Язык', loading: 'Загрузка документов…', loadError: 'Не удалось загрузить документы',
    choose: 'Выберите документ', accepted: 'Документ принят', accepting: 'Сохраняем согласие…', accept: 'Принять условия',
    signIn: 'Войти, чтобы принять условия', saveError: 'Не удалось сохранить согласие', acceptanceError: 'Не удалось загрузить ваши согласия',
    fallback: 'Этот проект пока доступен только на русском языке.'},
  en: {documents: 'Documents', title: 'Offers and terms', back: 'Home', available: 'Available documents',
    version: 'Version', published: 'Published', draft: 'Draft', publishedDocument: 'Published document',
    draftDocument: 'Draft document', language: 'Language', loading: 'Loading documents…', loadError: 'Could not load documents',
    choose: 'Select a document', accepted: 'Document accepted', accepting: 'Saving acceptance…', accept: 'Accept terms',
    signIn: 'Sign in to accept terms', saveError: 'Could not save acceptance', acceptanceError: 'Could not load your acceptances',
    fallback: 'This draft is currently available in Russian only.'},
  ka: {documents: 'დოკუმენტები', title: 'ოფერტები და პირობები', back: 'მთავარზე', available: 'ხელმისაწვდომი დოკუმენტები',
    version: 'ვერსია', published: 'გამოქვეყნებულია', draft: 'პროექტი', publishedDocument: 'გამოქვეყნებული დოკუმენტი',
    draftDocument: 'დოკუმენტის პროექტი', language: 'ენა', loading: 'დოკუმენტები იტვირთება…', loadError: 'დოკუმენტები ვერ ჩაიტვირთა',
    choose: 'აირჩიეთ დოკუმენტი', accepted: 'დოკუმენტი მიღებულია', accepting: 'თანხმობა ინახება…', accept: 'პირობების მიღება',
    signIn: 'პირობების მისაღებად შედით', saveError: 'თანხმობა ვერ შეინახა', acceptanceError: 'თქვენი თანხმობები ვერ ჩაიტვირთა',
    fallback: 'ეს პროექტი ჯერჯერობით მხოლოდ რუსულად არის ხელმისაწვდომი.'},
}

function InlineText({text}: {text: string}) {
  const parts = text.split(/(\*\*[^*]+\*\*|\[[^\]]+\]\(https?:\/\/[^)]+\)|https?:\/\/[^\s)]+)/g).filter(Boolean)
  return <>{parts.map((part, index) => {
    if (part.startsWith('**') && part.endsWith('**')) return <strong key={index}>{part.slice(2, -2)}</strong>
    const link = part.match(/^\[([^\]]+)\]\((https?:\/\/[^)]+)\)$/)
    if (link) return <a key={index} href={link[2]} target="_blank" rel="noreferrer">{link[1]}</a>
    if (part.startsWith('http')) return <a key={index} href={part} target="_blank" rel="noreferrer">{part}</a>
    return <Fragment key={index}>{part}</Fragment>
  })}</>
}

function DocumentText({content}: {content: string}) {
  return <div className="document-content">{content.split(/\r?\n/).map((line, index) => {
    const heading = line.match(/^(#{1,3})\s+(.+)/)
    const clause = line.match(/^(\d+)\.\s+(.+)/)
    if (heading?.[1] === '#') return <h1 key={index}><InlineText text={heading[2]}/></h1>
    if (heading) return <h2 className="document-section-title" key={index}><InlineText text={heading[2]}/></h2>
    if (clause) return <div className="document-clause" key={index}><span>{clause[1]}</span><p><InlineText text={clause[2]}/></p></div>
    if (!line.trim()) return <div className="document-space" key={index}/>
    return <p key={index}><InlineText text={line}/></p>
  })}</div>
}

export function OffersPage() {
  const [locale, setLocale] = useState<DocumentLocale>('ru')
  const [documents, setDocuments] = useState<LandingDocument[]>([])
  const [selectedType, setSelectedType] = useState<string | null>(null)
  const [error, setError] = useState(false)
  const {user} = useAuth()
  const [acceptances, setAcceptances] = useState<DocumentAcceptance[]>([])
  const [accepting, setAccepting] = useState(false)
  const [acceptError, setAcceptError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    void landingApi.documents(locale).then(items => {
      if (cancelled) return
      const visible = items.filter(item => !deferredDocumentTypes.has(item.type))
      setDocuments(visible)
      setSelectedType(current => visible.some(item => item.type === current) ? current : visible.find(item => item.published)?.type ?? visible[0]?.type ?? null)
      setError(false)
    }).catch(() => { if (!cancelled) setError(true) })
    return () => { cancelled = true }
  }, [locale])

  useEffect(() => {
    if (!user) { setAcceptances([]); return }
    void documentApi.acceptances().then(response => setAcceptances(response.data))
      .catch(() => setAcceptError(labels[locale].acceptanceError))
  }, [user])

  const t = labels[locale]
  const selected = documents.find(item => item.type === selectedType) ?? documents[0]
  const contract = selected?.published && selected.locale === locale && contractDocumentTypes.has(selected.type)
  const accepted = selected && acceptances.some(item => item.type === selected.type
    && item.version === selected.version && item.contentHash === selected.contentHash)

  async function acceptSelected() {
    if (!selected || !contract || accepting) return
    setAccepting(true)
    setAcceptError(null)
    try {
      const response = await documentApi.accept(selected)
      setAcceptances(items => [...items, response.data])
    } catch (cause) {
      setAcceptError(cause instanceof Error ? cause.message : t.saveError)
    } finally {
      setAccepting(false)
    }
  }
  return <main className="offers-page">
    <header className="offers-header"><Link className="landing-brand footer-brand" to="/"><img src={logo} alt=""/><span>Y.CS</span></Link><div><span>{t.documents}</span><b>{t.title}</b></div><Link className="offers-back" to="/">{t.back}</Link></header>
    <div className="offers-layout">
      <aside className="documents-nav"><div className="documents-nav-head"><span>{t.available}</span><b>{documents.length.toString().padStart(2, '0')}</b></div>{documents.map(document => <button type="button" className={document.type === selected?.type ? 'is-selected' : ''} key={`${document.type}-${document.version}`} onClick={() => { setSelectedType(document.type); setAcceptError(null) }}><span>{documentNames[locale][document.type] ?? document.type}</span><small>{t.version} {document.version.replace(/-(en|ka)$/, '')} · {document.published ? t.published : t.draft}</small></button>)}{!documents.length && !error && <div className="documents-state">{t.loading}</div>}{error && <div className="documents-state is-error">{t.loadError}</div>}</aside>
      <article className="document-view"><nav className="document-languages" aria-label={t.language}>{locales.map(option => <button type="button" key={option} lang={option} aria-pressed={locale === option} className={locale === option ? 'is-selected' : ''} onClick={() => { setDocuments([]); setLocale(option); setAcceptError(null) }}>{option === 'ru' ? 'Русский' : option === 'en' ? 'English' : 'ქართული'}</button>)}</nav>{selected ? <><div className="document-meta"><div><span>{selected.published ? t.publishedDocument : t.draftDocument}</span><b>{documentNames[locale][selected.type] ?? selected.type}</b></div><dl><div><dt>{t.version}</dt><dd>{selected.version.replace(/-(en|ka)$/, '')}</dd></div><div><dt>{t.language}</dt><dd>{selected.locale.toUpperCase()}</dd></div></dl></div>{selected.locale !== locale && <p className="document-language-fallback">{t.fallback}</p>}<DocumentText content={selected.content}/>{contract && <div className="document-acceptance">
        {accepted ? <span>{t.accepted}</span> : user
          ? <button type="button" disabled={accepting} onClick={() => void acceptSelected()}>{accepting ? t.accepting : t.accept}</button>
          : <Link to="/auth">{t.signIn}</Link>}
        {acceptError && <p role="alert">{acceptError}</p>}
      </div>}</> : !error && <div className="document-empty">{t.choose}</div>}</article>
    </div>
  </main>
}
