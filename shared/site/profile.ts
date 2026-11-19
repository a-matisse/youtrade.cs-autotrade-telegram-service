export interface SiteProfile {
  id: number
  login: string | null
  nickname: string
  firstName: string | null
  lastName: string | null
  avatar: {
    source: 'TELEGRAM' | 'DEFAULT' | 'UPLOADED'
    url: string | null
    uploadedImageAvailable: boolean
    imageId: string | null
    width: number | null
    height: number | null
    contentType: string | null
    byteSize: number | null
    urlExpiresAt: string | null
  }
  version: number
  profileInitializedAt: string
  createdAt: string
  updatedAt: string
}

export function profileGreeting(profile: SiteProfile | null) {
  return `Привет, ${profile?.nickname?.trim() || 'Пользователь'}`
}

export function profilePicture(profile: SiteProfile | null) {
  if (!profile || profile.avatar.source === 'DEFAULT' || !profile.avatar.url) return null
  const url = profile.avatar.url
  // Preserve the signed URL verbatim: rebuilding it can invalidate the signature.
  if (url.startsWith('/') && !url.startsWith('//')) return url
  try {
    const parsed = new URL(url)
    return parsed.protocol === 'https:' && !parsed.username && !parsed.password ? url : null
  } catch { return null }
}
