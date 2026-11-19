(() => {
  let loader
  let root
  let ready = false
  let cached = false
  let checkingCache = true
  let pendingReady = false
  let failed = false
  let observer
  let mountObserver
  let failureTimer
  let shownAt = performance.now()

  try {
    const url = new URL(location.href)
    const incomingTheme = url.searchParams.get('theme')
    if (incomingTheme === 'dark' || incomingTheme === 'light') {
      localStorage.setItem('ycs-theme', incomingTheme)
      url.searchParams.delete('theme')
      history.replaceState(history.state, '', url)
    }
    document.documentElement.dataset.bootTheme = localStorage.getItem('ycs-theme') === 'light' ? 'light' : 'dark'
    document.documentElement.dataset.siteTheme = document.documentElement.dataset.bootTheme
  } catch { /* Startup also works when browser storage is unavailable. */ }
  document.documentElement.classList.add('app-probing')

  const findElements = () => {
    loader = document.getElementById('app-loader')
    root = document.getElementById('root')
    return loader && root
  }

  const show = () => {
    if (ready || cached) return
    if (!findElements()) {
      mountObserver = new MutationObserver(() => {
        if (!findElements()) return
        mountObserver.disconnect()
        show()
      })
      mountObserver.observe(document.documentElement, {childList: true, subtree: true})
      return
    }
    loader.dataset.theme = document.documentElement.dataset.bootTheme || 'dark'
    loader.hidden = false
    shownAt = performance.now()
    root.setAttribute('inert', '')
    document.documentElement.classList.add('app-loading')
  }

  const fromCache = resources => {
    const entry = document.querySelector('script[type="module"][src]')
    return entry && resources.some(resource => resource.name === entry.src
      && resource.transferSize === 0 && resource.decodedBodySize > 0)
  }

  const fail = () => {
    if (ready || !findElements()) return
    failed = true
    checkingCache = false
    cached = false
    observer?.disconnect()
    mountObserver?.disconnect()
    window.clearTimeout(failureTimer)
    document.documentElement.classList.remove('app-probing')
    show()
    loader.dataset.state = 'failed'
    loader.querySelector('[data-loader-message]').textContent = 'Не удалось загрузить сайт.'
    loader.querySelector('[role="progressbar"]').hidden = true
    loader.querySelector('a').hidden = false
  }

  const onError = event => {
    if ((event.target instanceof HTMLScriptElement && event.target.type === 'module')
      || event instanceof ErrorEvent) fail()
  }
  const onPageShow = event => {
    if (event.persisted) finish()
  }
  const finish = () => {
    if (ready) return
    if (checkingCache) {
      pendingReady = true
      return
    }
    cached = cached || fromCache(performance.getEntriesByType('resource'))
    ready = true
    window.clearTimeout(failureTimer)
    observer?.disconnect()
    mountObserver?.disconnect()
    window.removeEventListener('error', onError, true)
    window.removeEventListener('pageshow', onPageShow)
    if (!findElements()) return
    const remove = () => {
      root.removeAttribute('inert')
      document.documentElement.classList.remove('app-probing', 'app-loading', 'app-entering')
      delete document.documentElement.dataset.bootTheme
      loader.remove()
      document.getElementById('app-loader-styles')?.remove()
    }
    if (cached || loader.hidden || window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      remove()
      return
    }
    window.setTimeout(() => {
      root.removeAttribute('inert')
      loader.dataset.state = 'ready'
      document.documentElement.classList.add('app-entering')
      window.setTimeout(remove, 400)
    }, Math.max(0, 280 - (performance.now() - shownAt)))
  }

  failureTimer = window.setTimeout(fail, 30_000)
  document.addEventListener('ycs:app-ready', finish, {once: true})
  window.addEventListener('error', onError, true)
  window.addEventListener('pageshow', onPageShow)

  if ('PerformanceObserver' in window) {
    try {
      observer = new PerformanceObserver(list => {
        if (fromCache(list.getEntries())) {
          cached = true
          mountObserver?.disconnect()
          if (!checkingCache) document.documentElement.classList.remove('app-probing', 'app-loading')
          if (findElements()) {
            root.removeAttribute('inert')
            loader.hidden = true
          }
        }
      })
      observer.observe({type: 'resource', buffered: true})
    } catch { /* The startup transition still works without resource timing support. */ }
  }

  const start = cacheHit => {
    if (ready || failed) return
    checkingCache = false
    cached = cached || cacheHit
    document.documentElement.classList.remove('app-probing')
    if (!cached) {
      document.documentElement.classList.add('app-loading')
      show()
    }
    if (pendingReady) finish()
  }
  const entryUrl = document.currentScript?.dataset.entry
  if (entryUrl) {
    fetch(entryUrl, {cache: 'only-if-cached', mode: 'same-origin'})
      .then(response => {
        response.body?.cancel().catch(() => {})
        start(response.ok)
      })
      .catch(() => start(false))
  } else start(false)
})()
