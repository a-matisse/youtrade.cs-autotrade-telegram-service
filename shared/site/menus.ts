export function closeHeaderMenus(header: HTMLElement, except?: HTMLDetailsElement) {
  header.querySelectorAll<HTMLDetailsElement>('details[open]').forEach(menu => {
    if (menu !== except) menu.open = false
  })
}

export function bindHeaderMenus(header: HTMLElement) {
  let restoringFocus = false
  let profileCloseTimer: number | undefined
  const outside = (event: Event) => {if (!header.contains(event.target as Node)) closeHeaderMenus(header)}
  const escape = (event: KeyboardEvent) => {
    if (event.key !== 'Escape') return
    restoringFocus = true
    header.querySelector<HTMLDetailsElement>('details[open]')?.querySelector('summary')?.focus()
    closeHeaderMenus(header)
    restoringFocus = false
  }
  const select = (event: Event) => {
    if ((event.target as Element).closest('a,button')) closeHeaderMenus(header)
  }
  const toggle = (event: Event) => {
    if (event.target instanceof HTMLDetailsElement && event.target.open) closeHeaderMenus(header, event.target)
  }
  const focusProfile = (event: FocusEvent) => {
    const element = event.target as Element
    if (restoringFocus || !element.matches('.site-profile-trigger:focus-visible')) return
    const menu = element.closest<HTMLDetailsElement>('.site-profile')
    if (menu) {closeHeaderMenus(header, menu); menu.open = true}
  }
  const blurProfile = (event: FocusEvent) => {
    const menu = (event.target as Element).closest<HTMLDetailsElement>('.site-profile')
    if (menu && !menu.contains(event.relatedTarget as Node | null)) menu.open = false
  }
  // Delegation also handles a profile that appears after the session loads.
  const pointerProfile = (event: PointerEvent) => {
    const menu = (event.target as Element).closest<HTMLDetailsElement>('.site-profile')
    if (!menu || menu.contains(event.relatedTarget as Node | null)) return
    if (event.type === 'pointerover') {
      if (event.pointerType === 'touch' || !window.matchMedia('(hover: hover)').matches) return
      window.clearTimeout(profileCloseTimer)
      closeHeaderMenus(header, menu)
      menu.open = true
    } else if (!menu.contains(document.activeElement)) {
      profileCloseTimer = window.setTimeout(() => {
        if (!menu.matches(':hover') && !menu.contains(document.activeElement)) menu.open = false
      }, 150)
    }
  }
  document.addEventListener('pointerdown', outside)
  document.addEventListener('keydown', escape)
  header.addEventListener('click', select)
  header.addEventListener('toggle', toggle, true)
  header.addEventListener('pointerover', pointerProfile)
  header.addEventListener('pointerout', pointerProfile)
  header.addEventListener('focusin', focusProfile)
  header.addEventListener('focusout', blurProfile)
  return () => {
    window.clearTimeout(profileCloseTimer)
    document.removeEventListener('pointerdown', outside)
    document.removeEventListener('keydown', escape)
    header.removeEventListener('click', select)
    header.removeEventListener('toggle', toggle, true)
    header.removeEventListener('pointerover', pointerProfile)
    header.removeEventListener('pointerout', pointerProfile)
    header.removeEventListener('focusin', focusProfile)
    header.removeEventListener('focusout', blurProfile)
  }
}
