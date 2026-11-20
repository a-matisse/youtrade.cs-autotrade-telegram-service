param(
    [uri]$BackendUrl = 'https://youtradecs.xyz',
    [ValidateRange(1, 65535)]
    [int]$Port = 5173
)

$ErrorActionPreference = 'Stop'

if (!$BackendUrl.IsAbsoluteUri -or $BackendUrl.Scheme -notin 'http', 'https' -or
    $BackendUrl.UserInfo -or $BackendUrl.Query -or $BackendUrl.Fragment) {
    throw 'BackendUrl must be an absolute HTTP or HTTPS URL without credentials, query or fragment.'
}

$backend = $BackendUrl.AbsoluteUri.TrimEnd('/')
$origin = "http://127.0.0.1:$Port"
$operationsPath = '/api/web/v1/overview/operations'
$webapp = Join-Path $PSScriptRoot '../webapp'
$npm = (Get-Command npm.cmd -ErrorAction Stop).Source

Write-Host "API backend: $backend"
try {
    $feed = Invoke-RestMethod -Uri "$backend$operationsPath" -TimeoutSec 5 -UseBasicParsing
    if ($feed.operations -isnot [array]) { throw 'Expected a JSON response with an operations array.' }
    Write-Host "Backend OK. Operations: $($feed.operations.Count)"
} catch {
    Write-Warning "Operation feed check failed at $backend$operationsPath. $($_.Exception.Message)"
}

$previousProxyTarget = $env:VITE_DEV_PROXY_TARGET
Push-Location -LiteralPath $webapp
try {
    if (!(Test-Path -LiteralPath 'node_modules/.bin/vite.cmd')) {
        & $npm ci
        if ($LASTEXITCODE -ne 0) { throw "npm ci failed with exit code $LASTEXITCODE." }
    }

    $env:VITE_DEV_PROXY_TARGET = $backend
    Write-Host "Sign-in page: $origin/auth"
    Write-Host "Operation feed through Vite: $origin$operationsPath"
    & $npm run dev -- --host 127.0.0.1 --port $Port --strictPort
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} finally {
    $env:VITE_DEV_PROXY_TARGET = $previousProxyTarget
    Pop-Location
}
