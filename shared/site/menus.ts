export function closeHeaderMenus(header: HTMLElement, except?: HTMLDetailsElement) {
  header.querySelectorAll<HTMLDetailsElement>('details[open]').forEach(menu => {
    if (menu !== except) menu.open = false
  })
}

export function bindHeaderMenus(header: HTMLElement) {
  const outside = (event: Event) => {if (!header.contains(event.target as Node)) closeHeaderMenus(header)}
  const escape = (event: KeyboardEvent) => {
    if (event.key !== 'Escape') return
    header.querySelector<HTMLDetailsElement>('details[open]')?.querySelector('summary')?.focus()
    closeHeaderMenus(header)
  }
  const select = (event: Event) => {
    if ((event.target as Element).closest('a,button')) closeHeaderMenus(header)
  }
  const toggle = (event: Event) => {
    if (event.target instanceof HTMLDetailsElement && event.target.open) closeHeaderMenus(header, event.target)
  }
  document.addEventListener('pointerdown', outside)
  document.addEventListener('keydown', escape)
  header.addEventListener('click', select)
  header.addEventListener('toggle', toggle, true)
  return () => {
    document.removeEventListener('pointerdown', outside)
    document.removeEventListener('keydown', escape)
    header.removeEventListener('click', select)
    header.removeEventListener('toggle', toggle, true)
  }
}
