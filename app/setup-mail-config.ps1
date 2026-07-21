$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$configDir = Join-Path $scriptDir "local"
$configPath = Join-Path $configDir "application-mail.properties"

$senderEmail = Read-Host "Gmail adresa sa koje aplikacija salje"
if ([string]::IsNullOrWhiteSpace($senderEmail)) {
    Write-Error "Sender email je obavezan."
    exit 1
}

$adminRecipients = Read-Host "Dodatni admin mailovi (opciono, odvojeni zarezom)"
$securePassword = Read-Host "Gmail App Password" -AsSecureString
$passwordPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $appPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPtr)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPtr)
}

if ([string]::IsNullOrWhiteSpace($appPassword)) {
    Write-Error "Gmail App Password je obavezan."
    exit 1
}

New-Item -ItemType Directory -Force -Path $configDir | Out-Null

$content = @(
    "app.mail.enabled=true",
    "app.mail.from=$senderEmail",
    "app.mail.admin-recipients=$adminRecipients",
    "",
    "spring.mail.host=smtp.gmail.com",
    "spring.mail.port=587",
    "spring.mail.username=$senderEmail",
    "spring.mail.password=$appPassword",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=true"
)

Set-Content -Path $configPath -Value $content -Encoding ASCII

Write-Host ""
Write-Host "Mail konfiguracija je sacuvana u: $configPath"
Write-Host "Sada restartuj backend da bi nova podesavanja bila ucitana."
