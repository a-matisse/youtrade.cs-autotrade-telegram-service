import {Navigate, Outlet, useLocation} from 'react-router-dom'
import {useAuth} from '../auth/authState'
import {SessionLoading} from '../components/ui/States'

export function PublicOnly() {
    const {loading, token, user} = useAuth();
    if (loading) return <Outlet/>;
    if (token && user) return <Navigate to="/dashboard" replace/>;
    return <Outlet/>
}

export function RequireAuth() {
    const {loading, token, user} = useAuth();
    const location = useLocation();
    if (loading) return <SessionLoading/>;
    if (!token || !user) return <Navigate to="/auth" state={{from: location}} replace/>;
    return <Outlet/>
}
