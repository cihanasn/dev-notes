<#import "template.ftl" as layout>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>
    <p>Merhaba ${user.firstName!},</p>
    
    <p>Giriş doğrulama kodunuz:</p>
    
    <h2 style="letter-spacing: 4px; color: #333;">${otp}</h2>
    
    <p>Bu kod <strong>5 dakika</strong> geçerlidir.</p>
    
    <p>Eğer bu işlemi siz yapmadıysanız, bu e-postayı dikkate almayınız.</p>
</body>
</html>