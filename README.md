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

<img width="177" height="102" alt="image" src="https://github.com/user-attachments/assets/3c1aa9db-4864-4492-9868-a01f0949d8f5" />


``` markdown
com.cihan.keycloak.otp
```
package oluştur.

- src/main/java’ya sağ tık → New → Package
- Package adı olarak: com.cihan.keycloak.otp yaz

<img width="368" height="408" alt="image" src="https://github.com/user-attachments/assets/86cd91cc-73c1-4261-a138-3937125f6376" />

## İlk Class

Bu class OTP kontrolünü yapacak.

``` markdown
OtpAuthenticator.java
```

``` markdown
package com.cihan.keycloak.otp;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.UserModel;

public class OtpAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        System.out.println("OTP step started");
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(org.keycloak.models.KeycloakSession session,
                                 org.keycloak.models.RealmModel realm,
                                 UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(org.keycloak.models.KeycloakSession session,
                                   org.keycloak.models.RealmModel realm,
                                   UserModel user) {
    }

    @Override
    public void close() {
    }
}
```
Şimdilik sadece OTP step başladı yazdırıyoruz.

``` markdown
package com.cihan.keycloak.otp;

import java.util.Collections;
import java.util.List;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

public class OtpAuthenticatorFactory implements AuthenticatorFactory, Authenticator {

    public static final String ID = "turkce-otp";

    // ================= FACTORY =================

    @Override
    public Authenticator create(KeycloakSession session) {
        return this;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayType() {
        return "Türkçe OTP Doğrulama";
    }

    @Override
    public String getHelpText() {
        return "Kullanıcıya OTP doğrulama adımı ekler";
    }

    @Override
    public String getReferenceCategory() {
        return "otp";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[]{
                Requirement.REQUIRED,
                Requirement.ALTERNATIVE,
                Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    // ================= AUTHENTICATOR =================

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        System.out.println("Türkçe OTP adımı başladı");
        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        context.success();
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session,
                                  org.keycloak.models.RealmModel realm,
                                  UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session,
                                   org.keycloak.models.RealmModel realm,
                                   UserModel user) {
    }
}
```

### SPI file (ZORUNLU)

``` markdown
src/main/resources/META-INF/services/org.keycloak.authentication.AuthenticatorFactory
```

İçine:

``` markdown
com.cihan.keycloak.otp.OtpAuthenticatorFactory
```

<img width="1143" height="272" alt="image" src="https://github.com/user-attachments/assets/1984dedb-6114-4d2c-b729-160939e2341b" />

## Maven ile build

<img width="849" height="131" alt="image" src="https://github.com/user-attachments/assets/d073e8ee-d1b6-4eb4-95f0-a35928d49912" />

Terminalde proje klasöründe:

``` markdown
mvn clean package
```
çıktı:

``` markdown
target/keycloak-otp-authenticator-1.0.0.jar
```

Bu dosya bizim plugin.

<img width="1184" height="238" alt="image" src="https://github.com/user-attachments/assets/d4a87e31-8833-4105-bf9b-633636da1732" />

## Jar’ı Keycloak’a koyma

Docker container içine:

``` markdown
/opt/keycloak/providers
```

klasörüne koyacağız.

### Container içine dosya kopyalama

``` markdown
docker cp target/keycloak-otp-authenticator-1.0.0.jar keycloak-test:/opt/keycloak/providers/
```

<img width="1423" height="63" alt="image" src="https://github.com/user-attachments/assets/e4855540-d238-4be7-9884-dd809d704962" />

Container içine girip kontrol (opsiyonel)

<img width="777" height="147" alt="image" src="https://github.com/user-attachments/assets/6c77c010-7648-4014-b094-73311212206d" />

Sonra:

``` markdown
- docker exec -it keycloak-test /bin/bash
- cd /opt/keycloak
- bin/kc.sh build
- exit
- docker restart keycloak-test
```

<img width="626" height="73" alt="image" src="https://github.com/user-attachments/assets/1e4ae8e7-dc6b-44a6-a454-2b538b7872ea" />

## Admin panelden flow'a ekleme

Keycloak Admin Console:

``` markdown
Authentication
   → Flows
      → Browser
         → Add step
            → Türkçe OTP Doğrulama
```

<img width="1438" height="924" alt="image" src="https://github.com/user-attachments/assets/604b39ec-2662-4ebe-8c05-835626e64710" />

### Admin panel:

``` markdown
Authentication → Flows
```

👉 “Browser” flow’u bul

👉 sağdan:

``` markdown
Copy
```

Örnek isim:

``` markdown
browser-with-otp
```

### Step ekle

Artık şuradan:

``` markdown
Add step
```

👉 senin OTP provider:

``` markdown
Türkçe OTP Doğrulama
```

### Required yap
- REQUIRED seç

### Bind et
Bindings → Browser Flow

👉 seç:

``` markdown
browser-with-otp
```

<img width="586" height="155" alt="image" src="https://github.com/user-attachments/assets/45ff8013-53f9-47c5-b996-67d196054313" />

``` bash
docker run -d --name keycloak-test -p 9090:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.0 start-dev
```




