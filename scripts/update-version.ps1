$info = Get-Content .\mod\EnableTranspONder\mod_info.json | ConvertFrom-Json
$s = $info.version.split('-');
$ver = [version]::Parse($s[0]);
$verJson = Get-Content .\mod\EnableTranspONder\EnableTranspONder.version | ConvertFrom-Json
$verJson.modVersion.major = $ver.Major;
$verJson.modVersion.minor = $ver.Minor;
$suffix = if ($s.Length -gt 1) { "-" + $s[1] } else { "" }
$verJson.modVersion.patch = $ver.Build.ToString() + $suffix;;
$verJson | ConvertTo-Json | Out-File -Encoding utf8 .\mod\EnableTranspONder\EnableTranspONder.version