let token: string | null = null
let expiresAt = 0

export const tokenStorage = {
  get: () => token,
  expiresAt: () => expiresAt,
  set: (value: string, until: number) => { token = value; expiresAt = until },
  clear: () => {
    token = null
    expiresAt = 0
    try {
      sessionStorage.removeItem('youtrade.jwt')
      localStorage.removeItem('youtrade.jwt')
    } catch { /* The session itself does not depend on browser storage. */ }
  },
}
