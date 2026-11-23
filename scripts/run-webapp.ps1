[CmdletBinding(DefaultParameterSetName = 'Backend')]
param(
    [Parameter(ParameterSetName = 'Backend')]
    [uri]$BackendUrl = 'https://youtradecs.xyz',
    [Parameter(Mandatory, ParameterSetName = 'Mock')]
    [switch]$Mock,
    [Parameter(ParameterSetName = 'Mock')]
    [ValidateRange(1, 65535)]
    [int]$MockPort = 4921,
    [ValidateRange(1, 65535)]
    [int]$Port = 5173,
    [ValidatePattern('^/(?!/)[a-zA-Z0-9/_?&=.#%-]*$')]
    [string]$Page = '/auth'
)

$ErrorActionPreference = 'Stop'

if (!$BackendUrl.IsAbsoluteUri -or $BackendUrl.Scheme -notin 'http', 'https' -or
    $BackendUrl.UserInfo -or $BackendUrl.Query -or $BackendUrl.Fragment) {
    throw 'BackendUrl must be an absolute HTTP or HTTPS URL without credentials, query or fragment.'
}

$repo = Split-Path -Parent $PSScriptRoot
$backend = if ($Mock) { "http://127.0.0.1:$MockPort/backend" } else { $BackendUrl.AbsoluteUri.TrimEnd('/') }
$origin = "http://127.0.0.1:$Port"
$operationsPath = '/api/web/v1/overview/operations'
$webapp = Join-Path $repo 'webapp'
$npm = (Get-Command npm.cmd -ErrorAction Stop).Source

function Read-OperationFeed {
    $feed = Invoke-RestMethod -Uri "$backend$operationsPath" -TimeoutSec 5 -UseBasicParsing
    if ($feed.operations -isnot [array]) { throw 'Expected a JSON response with an operations array.' }
    return $feed
}

$previousProxyTarget = $env:VITE_DEV_PROXY_TARGET
$simulation = $null
$simulationLogs = @()
Push-Location -LiteralPath $webapp
try {
    if ($Mock -and $Port -eq $MockPort) { throw 'Port and MockPort must be different.' }
    if (!(Test-Path -LiteralPath 'node_modules/.bin/vite.cmd')) {
        & $npm ci
        if ($LASTEXITCODE -ne 0) { throw "npm ci failed with exit code $LASTEXITCODE." }
    }

    Write-Host "API backend: $backend"
    if ($Mock) {
        $postman = (Get-Command postman.exe -ErrorAction Stop).Source
        $portCheck = [Net.Sockets.TcpListener]::new([Net.IPAddress]::Loopback, $MockPort)
        try { $portCheck.Start() }
        catch { throw "Postman port $MockPort is busy. Choose another port with -MockPort." }
        finally { $portCheck.Stop() }

        $simulationLogs = @([IO.Path]::GetTempFileName(), [IO.Path]::GetTempFileName())
        $simulation = Start-Process -FilePath $postman -WorkingDirectory $repo -PassThru -WindowStyle Hidden `
            -ArgumentList @('simulation', 'run', 'postman/simulations/web-auth.sim.yaml', '--port', $MockPort, '--no-history') `
            -RedirectStandardOutput $simulationLogs[0] -RedirectStandardError $simulationLogs[1]
        $deadline = [DateTime]::UtcNow.AddSeconds(30)
        while ($true) {
            if ($simulation.HasExited -or [DateTime]::UtcNow -ge $deadline) {
                $diagnostic = Get-Content -LiteralPath $simulationLogs[1] -Tail 10 -ErrorAction SilentlyContinue
                throw "Postman simulation did not start on port $MockPort. $diagnostic"
            }
            try { $feed = Read-OperationFeed; break }
            catch { Start-Sleep -Milliseconds 250 }
        }
        Write-Host "Local sample data ready. Operations: $($feed.operations.Count)"
        Write-Host "Use the Telegram button for the local sign-in flow."
    } else {
        try {
            $feed = Read-OperationFeed
            Write-Host "Backend OK. Operations: $($feed.operations.Count)"
        } catch {
            Write-Warning "Operation feed check failed at $backend$operationsPath. $($_.Exception.Message)"
        }
    }

    $env:VITE_DEV_PROXY_TARGET = $backend
    Write-Host "Open: $origin$Page"
    Write-Host "Operation feed through Vite: $origin$operationsPath"
    & $npm run dev -- --host 127.0.0.1 --port $Port --strictPort
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} finally {
    if ($null -ne $simulation -and !$simulation.HasExited) {
        & taskkill.exe /PID $simulation.Id /T /F | Out-Null
        $simulation.WaitForExit()
    }
    if ($simulationLogs.Count) { Remove-Item -LiteralPath $simulationLogs -ErrorAction SilentlyContinue }
    $env:VITE_DEV_PROXY_TARGET = $previousProxyTarget
    Pop-Location
}
