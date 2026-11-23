import {useState, type SelectHTMLAttributes} from 'react'
import icons from '../../../../shared/site/icons.svg?no-inline'

export function PortfolioFilter({label, ...props}: SelectHTMLAttributes<HTMLSelectElement> & {label: string}) {
  const [pointerFocus, setPointerFocus] = useState(false)

  return <label className="portfolio-filter"
    onPointerDown={event => { if (event.button === 0 && !props.disabled) setPointerFocus(true) }}
    onKeyDown={() => setPointerFocus(false)}
    onBlur={() => setPointerFocus(false)}>
    <span>{label}</span>
    <span className="portfolio-select">
      <select {...props} data-pointer-focus={pointerFocus || undefined}/>
      <svg aria-hidden="true"><use href={`${icons}#chevron-down`}/></svg>
    </span>
  </label>
}
