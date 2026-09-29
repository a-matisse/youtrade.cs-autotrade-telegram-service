param(
    [string]$OverviewUrl = 'https://youtradecs.xyz/api/web/v1/overview',
    [string]$Output = 'webapp/public/chart-preview-2026-09-29.png',
    [double]$AgentFeePercent = 3
)

Add-Type -AssemblyName System.Drawing
$overview = Invoke-RestMethod -Uri $OverviewUrl -TimeoutSec 20
$daily = @($overview.statistics.daily)
if ($daily.Count -lt 2) { throw 'The overview has too few daily values for a chart.' }

$width = 1200
$height = 630
$bitmap = [System.Drawing.Bitmap]::new($width, $height)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit

function Color([string]$hex) { return [System.Drawing.ColorTranslator]::FromHtml($hex) }
function DrawText([string]$value, [float]$x, [float]$y, [float]$size, [string]$color, [bool]$bold = $false) {
    $style = if ($bold) { [System.Drawing.FontStyle]::Bold } else { [System.Drawing.FontStyle]::Regular }
    $font = [System.Drawing.Font]::new('Segoe UI', $size, $style)
    $brush = [System.Drawing.SolidBrush]::new((Color $color))
    try { $graphics.DrawString($value, $font, $brush, $x, $y) }
    finally { $brush.Dispose(); $font.Dispose() }
}

try {
    $graphics.Clear((Color '#F9F7F2'))
    $border = [System.Drawing.Pen]::new((Color '#CEC5B7'), 2)
    $rule = [System.Drawing.Pen]::new((Color '#E4DDD2'), 1)
    $grid = [System.Drawing.Pen]::new((Color '#E6E0D6'), 1)
    $grossPen = [System.Drawing.Pen]::new((Color '#B8323A'), 4)
    $netPen = [System.Drawing.Pen]::new((Color '#35373B'), 4)
    try {
        $graphics.DrawRectangle($border, 1, 1, $width - 3, $height - 3)
        DrawText 'Y.CS' 50 27 31 '#17191D' $true
        DrawText 'РЕЗУЛЬТАТ ПРОДАЖ' 50 83 25 '#17191D' $true
        DrawText 'ПОСЛЕДНИЕ 30 ДНЕЙ · USD' 824 93 14 '#72716D' $true
        $graphics.DrawLine($rule, 50, 138, 1150, 138)

        $gross = @($daily | ForEach-Object { [double]$_.revenue - [double]$_.cost })
        $net = @($daily | ForEach-Object { [double]$_.revenue * (1 - $AgentFeePercent / 100) - [double]$_.cost })
        $minimum = [math]::Floor(([math]::Min(0, ($gross + $net | Measure-Object -Minimum).Minimum)) / 100) * 100
        $maximum = [math]::Ceiling(([math]::Max(0, ($gross + $net | Measure-Object -Maximum).Maximum)) / 100) * 100
        if ($maximum -le $minimum) { $maximum = $minimum + 100 }
        $left = 125.0
        $top = 170.0
        $plotWidth = 1010.0
        $plotHeight = 310.0
        for ($i = 0; $i -le 4; $i++) {
            $y = [float]($top + $i * $plotHeight / 4)
            $graphics.DrawLine($grid, [float]$left, $y, [float]($left + $plotWidth), $y)
            $value = $maximum - $i * ($maximum - $minimum) / 4
            DrawText ('$' + $value.ToString('N0', [cultureinfo]::GetCultureInfo('en-US'))) 45 ($y - 11) 13 '#8C8881'
        }
        $grossPoints = [System.Drawing.PointF[]]::new($daily.Count)
        $netPoints = [System.Drawing.PointF[]]::new($daily.Count)
        for ($i = 0; $i -lt $daily.Count; $i++) {
            $x = [float]($left + $i * $plotWidth / ($daily.Count - 1))
            $grossPoints[$i] = [System.Drawing.PointF]::new($x, [float]($top + ($maximum - $gross[$i]) / ($maximum - $minimum) * $plotHeight))
            $netPoints[$i] = [System.Drawing.PointF]::new($x, [float]($top + ($maximum - $net[$i]) / ($maximum - $minimum) * $plotHeight))
        }
        $graphics.DrawLines($grossPen, $grossPoints)
        $graphics.DrawLines($netPen, $netPoints)
        DrawText $daily[0].date 124 488 13 '#72716D'
        DrawText $daily[-1].date 1038 488 13 '#72716D'
        $grossBrush = [System.Drawing.SolidBrush]::new((Color '#B8323A'))
        $netBrush = [System.Drawing.SolidBrush]::new((Color '#35373B'))
        try {
            $graphics.FillRectangle($grossBrush, 50, 536, 23, 4)
            $graphics.FillRectangle($netBrush, 305, 536, 23, 4)
        } finally { $grossBrush.Dispose(); $netBrush.Dispose() }
        DrawText 'Без комиссии' 82 522 15 '#555C57'
        DrawText ("После {0}%" -f $AgentFeePercent) 337 522 15 '#555C57'
        $stamp = [datetimeoffset]::Parse($overview.generatedAt).ToUniversalTime().ToString('dd.MM.yyyy HH:mm', [cultureinfo]::InvariantCulture)
        DrawText ("Данные: {0} UTC" -f $stamp) 805 522 15 '#555C57'
        $graphics.DrawLine($rule, 50, 574, 1150, 574)
        DrawText 'youtradecs.xyz' 50 585 13 '#72716D'
    } finally {
        $border.Dispose(); $rule.Dispose(); $grid.Dispose(); $grossPen.Dispose(); $netPen.Dispose()
    }
    $outputPath = [System.IO.Path]::GetFullPath($Output)
    $bitmap.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Output ("Saved {0} from {1}" -f $outputPath, $overview.generatedAt)
} finally {
    $graphics.Dispose()
    $bitmap.Dispose()
}
