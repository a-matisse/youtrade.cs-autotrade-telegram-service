import {useEffect, useRef, useState} from 'react'
import icons from '../../../../shared/site/icons.svg?no-inline'
import {profilePicture, type SiteProfile} from '../../../../shared/site/profile'

export function ProfileAvatar({profile, refresh}: {profile: SiteProfile | null; refresh(): Promise<SiteProfile | null>}) {
    const picture = profilePicture(profile)
    const [failedUrl, setFailedUrl] = useState<string | null>(null)
    const failedAsset = useRef<string | null>(null)
    const renewedExpiry = useRef<string | null>(null)
    const asset = profile ? `${profile.id}:${profile.version}:${profile.avatar.source}:${profile.avatar.imageId}` : ''
    useEffect(() => {
        const expiresAt = profile?.avatar.urlExpiresAt
        if (!picture || !expiresAt) return
        const expiry = Date.parse(expiresAt)
        if (!Number.isFinite(expiry) || renewedExpiry.current === picture + expiresAt) return
        const timer = window.setTimeout(() => {
            renewedExpiry.current = picture + expiresAt
            void refresh()
        }, Math.min(2_147_483_647, Math.max(0, expiry - Date.now() - 30_000)))
        return () => window.clearTimeout(timer)
    }, [picture, profile?.avatar.urlExpiresAt, refresh])
    const onError = () => {
        setFailedUrl(picture)
        if (failedAsset.current === asset) return
        failedAsset.current = asset
        void refresh()
    }
    return picture && failedUrl !== picture
        ? <img className="site-avatar" src={picture} alt="" referrerPolicy="no-referrer" onError={onError}/>
        : <svg className="site-avatar-placeholder" aria-hidden="true"><use href={`${icons}#profile`}/></svg>
}
