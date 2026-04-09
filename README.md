# dev-notes

``` bash
docker run -d --name keycloak-test -p 9090:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.0 start-dev
```

<img width="989" height="345" alt="image" src="https://github.com/user-attachments/assets/f0fd9d8b-083e-4a3d-8efb-fc1acdce168f" />

## Adımlar:

Docker Desktop açık olsun
CMD aç
Yukarıdaki komutu yapıştır
Enter

Sonra tarayıcıdan:

``` bash
http://localhost:9090
```

adresine girersin.

Login:

``` bash
username: admin
password: admin
```

## Java ile OTP uygulaması

OTP plugin geliştirme teknolojileri

### Genelde şu stack kullanılır:

- Java 17+
- Maven
- Keycloak SPI: Keycloak’ın tüm extension sistemi Java SPI (Service Provider Interface) üzerine kuruludur.
- .jar plugin

## Eclipse’te Maven Projesi Oluşturma

``` markdown
File
 → New
   → Maven Project
```
Sonra:

``` markdown
Create a simple project (skip archetype selection)
```

kutusunu işaretle ve Next.

<img width="384" height="181" alt="image" src="https://github.com/user-attachments/assets/e988a87b-d798-4646-9bba-97fa4aff7ab1" />

Proje Bilgileri

Şöyle doldur:

``` markdown
Group Id: com.cihan.keycloak
Artifact Id: keycloak-otp-authenticator
Version: 1.0.0
Packaging: jar
```

Finish.

<img width="994" height="681" alt="image" src="https://github.com/user-attachments/assets/4d95d7aa-f9a1-42b7-88e0-617f36b9852f" />

Eclipse şu yapıyı oluşturur:

``` markdown
keycloak-otp-authenticator
 ├─ src/main/java
 ├─ src/test/java
 └─ pom.xml
```
### pom.xml içine Keycloak dependency ekleme

**pom.xml** dosyasını aç ve içine şunu ekle.

``` markdown
<dependencies>

    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-server-spi</artifactId>
        <version>26.0.0</version>
    </dependency>

    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-server-spi-private</artifactId>
        <version>26.0.0</version>
    </dependency>

    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-services</artifactId>
        <version>26.0.0</version>
    </dependency>

</dependencies>
```
Sonra Maven → Update Project yap.

## Java Package oluştur

src/main/java altında:

``` markdown
com.cihan.keycloak.otp
```
package oluştur.



