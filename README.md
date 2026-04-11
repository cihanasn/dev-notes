# dev-notes

**Authenticator SPI (Kimlik Doğrulayıcı Hizmet Sağlayıcı Arayüzü)**, bir uygulamanın, kullanıcıların kimliğini doğrulama (authentication) yöntemini, uygulamanın temel kodunu değiştirmeye gerek kalmadan, esnek bir şekilde değiştirmesine veya yeni yöntemler eklemesine olanak tanıyan bir yazılım arayüzüdür.

Bunu bir priz veya USB girişi gibi düşünebilirsiniz. Prizin şekli (SPI), hangi cihazı takarsanız takın (örneğin, telefon şarj aleti, saç kurutma makinesi) elektrik almasını sağlayan standarttır. İşte Authenticator SPI de bir uygulamanın kimlik doğrulama "prizi"dir.

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

Keycloak’ta login süreci Authentication Flow adı verilen bir pipeline’dır.
Bu pipeline’a yeni bir adım eklemek için Authenticator yazılır.

- authenticate() metodu: Bu metod login sırasında çalışır. Kullanıcı login olmaya çalıştığında Keycloak bu metodu çağırır. (OTP form göster.)
- action() metodu: Bu metod kullanıcı OTP girdikten sonra çalışır. (Kullanıcı OTP girer.) Yani burada yapılması gereken şey: OTP doğru mu kontrol et
- requiresUser() metodu: true demek: username/password login yapılmış olmalı
Yani OTP sadece login sonrası çalışır.
- configuredFor() metodu: Bu metod şunu kontrol eder: Bu kullanıcı için OTP aktif mi?
Kullanıcının OTP ayarı var mı? Ama şu anda: return true, yani her kullanıcı için OTP var kabul ediyor.

- setRequiredActions() metodu:

``` markdown
Bu metod şunu yapar:
kullanıcı OTP setup yapmadıysa
OTP setup zorunlu yap
Mesela: Google Authenticator setup
Ama şu anda boş.
```

- close() metodu:

``` markdown
Bu metod resource cleanup için vardır.
Mesela:
db connection kapat
cache temizle
Ama çoğu plugin'de boş bırakılır.
```

Aslında şu anda hiç OTP yapmıyor.

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
Bu sınıf hem AuthenticatorFactory hem de Authenticator implement ediyor. Bu yüzden hem plugin’i tanımlıyor hem de çalıştırıyor.

Normalde production projelerde bu ikisi ayrı sınıf olur. Ama burada basit olsun diye aynı sınıfa yazılmış.

Şimdi şu sabite bakalım.

``` markdown
public static final String ID = "turkce-otp";
```

Bu plugin’in benzersiz kimliğidir. Keycloak plugin’i yüklediğinde bunu kullanır.
Admin panelde flow eklerken aslında Keycloak şunu arar: turkce-otp

create() metodu: Bu metod Keycloak login sırasında Authenticator instance üretir.
Normalde şöyle olur:
``` markdown
return new OtpAuthenticator();
```
Ama burada sınıf zaten Authenticator olduğu için: return this

getDisplayType() metodu: Bu admin panelde görünen isimdir.

init() metodu: Keycloak plugin’i ilk yüklediğinde çalışır.

postInit() metodu: Keycloak tamamen ayağa kalktıktan sonra çalışır. Plugin’ler arası bağlantı burada yapılabilir.

close() metodu: Plugin kapatılırken çalışır. Resource cleanup yapılır.

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
Duplicate
```

<img width="1539" height="113" alt="image" src="https://github.com/user-attachments/assets/3fd0cb90-e8bc-4ba4-a720-8713765a11e5" />

Örnek isim:

``` markdown
browser-with-otp
```

<img width="642" height="298" alt="image" src="https://github.com/user-attachments/assets/6d39ec54-2783-4d2e-bf36-b8288c171045" />

### Step ekle

Artık şuradan:

``` markdown
Add step
```

👉 senin OTP provider:

``` markdown
Türkçe OTP Doğrulama
```

<img width="964" height="492" alt="image" src="https://github.com/user-attachments/assets/8d983051-55d1-45c4-8f67-bc5298cdb975" />

### Required yap
- REQUIRED seç

<img width="742" height="81" alt="image" src="https://github.com/user-attachments/assets/baedeba0-e232-42bf-b16a-b083b7bdc770" />

### Bind et
Bindings → Browser Flow

<img width="1573" height="228" alt="image" src="https://github.com/user-attachments/assets/95159e2c-c31e-400c-ba52-d8b040fad76e" />

👉 seç:

``` markdown
browser-with-otp
```

<img width="643" height="232" alt="image" src="https://github.com/user-attachments/assets/5a60097f-bc89-46b3-a836-99e5e6ce4702" />

<img width="586" height="155" alt="image" src="https://github.com/user-attachments/assets/45ff8013-53f9-47c5-b996-67d196054313" />

``` bash
docker run -d --name keycloak-test -p 9090:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.0 start-dev
```
## Gerçek OTP Authenticator nasıl olur?

Gerçek versiyon şöyle çalışır:

``` markdown
authenticate()

generateOTP()
storeOTP()
sendOTP()
showOTPForm()
```

Sonra:

``` markdown
action()

readOTP()
validateOTP()
loginSuccess()
```

``` markdown
User login
↓
Authentication Flow
↓
OtpAuthenticator.authenticate()
↓
OTP ekranı
↓
OtpAuthenticator.action()
↓
OTP doğrulama
↓
Login success
```

Eğer bunu tam yazarsan:

``` markdown
SMS OTP
Email OTP
Google Authenticator
```

yapabilirsin.

## Bir sonraki adımda gerçek çalışan OTP Authenticator

Önce hardcoded 123456 OTP ile çalışan tam bir flow yazıyorum.

OtpAuthenticatorFactory.java — mevcut dosyanı tamamen bununla değiştir:

``` markdown
package com.cihan.keycloak.otp;

import java.util.Collections;
import java.util.List;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class OtpAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "turkce-otp";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new OtpAuthenticator();
    }

    @Override
    public String getId() { return ID; }

    @Override
    public String getDisplayType() { return "Türkçe OTP Doğrulama"; }

    @Override
    public String getHelpText() { return "Email ile OTP doğrulama"; }

    @Override
    public String getReferenceCategory() { return "otp"; }

    @Override
    public boolean isConfigurable() { return false; }

    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[]{
            Requirement.REQUIRED,
            Requirement.ALTERNATIVE,
            Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() { return false; }

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
}
```

OtpAuthenticator.java — mevcut dosyanı tamamen bununla değiştir:

``` markdown
package com.cihan.keycloak.otp;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class OtpAuthenticator implements Authenticator {

    private static final String HARDCODED_OTP = "123456";
    private static final String OTP_FORM_TEMPLATE = "otp-form.ftl";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // Formu göster
        Response response = context.form()
            .createForm(OTP_FORM_TEMPLATE);
        context.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // Kullanıcının girdiği kodu al
        MultivaluedMap<String, String> formData =
            context.getHttpRequest().getDecodedFormParameters();
        String enteredOtp = formData.getFirst("otp");

        if (HARDCODED_OTP.equals(enteredOtp)) {
            context.success();
        } else {
            Response response = context.form()
                .setError("Hatalı OTP kodu. Lütfen tekrar deneyin.")
                .createForm(OTP_FORM_TEMPLATE);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, response);
        }
    }

    @Override
    public boolean requiresUser() { return true; }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}
}
```

themes klasörü altında kendimiz oluşturacağız:

``` markdown
docker exec -it keycloak-test mkdir -p /opt/keycloak/themes/custom/login
```

Yeni dosya: otp-form.ftl
Bu dosya Keycloak'ın theme klasörüne gidecek. Yolu: themes/custom/login/otp-form.ftl

Container içindeki tam yol: /opt/keycloak/themes/custom/login/otp-form.ftl

İçeriği:

``` markdown
<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        OTP Doğrulama
    <#elseif section = "form">
        <form action="${url.loginAction}" method="post">
            <div class="form-group">
                <label for="otp">OTP Kodunuzu Girin</label>
                <input type="text" id="otp" name="otp"
                       class="form-control"
                       autofocus
                       autocomplete="off"
                       placeholder="6 haneli kod"/>
            </div>

            <#if message?has_content>
                <div class="alert alert-error">
                    ${message.summary}
                </div>
            </#if>

            <div class="form-group">
                <input type="submit" value="Doğrula" class="btn btn-primary btn-block"/>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
```

.ftl dosyasını container'a kopyalama komutu:

``` markdown
docker cp otp-form.ftl keycloak-test:/opt/keycloak/themes/custom/login/otp-form.ftl
```

<img width="1418" height="91" alt="image" src="https://github.com/user-attachments/assets/7ffea095-cc5c-4314-8f6b-f509ee75b10a" />

``` markdown
docker exec -it keycloak-test find /opt/keycloak -name "*.jar"
/opt/keycloak/lib/lib/main/org.keycloak.keycloak-themes-26.0.8.jar
```

Base temalar bu jar içinde. Bizim otp-form.ftl dosyasını oraya koymak yerine, en temiz çözüm şu:
Container içinde themes/custom/login klasörünü oluşturup theme.properties ile base'i extend etmek. Bunu şimdi tek komutla yapalım:

Şimdi theme.properties dosyasını oluştur.

``` markdown
parent=base
```

``` markdown
docker cp theme.properties keycloak-test:/opt/keycloak/themes/custom/login/theme.properties
```

<img width="793" height="66" alt="image" src="https://github.com/user-attachments/assets/0f62ad29-5d12-4a6c-a052-94d259ceea41" />

Şimdi build + restart:

``` markdown
docker exec -it keycloak-test /bin/bash -c "cd /opt/keycloak && bin/kc.sh build"
docker restart keycloak-test
```
Sonra Admin panelden: 

``` markdown
Realm Settings → Themes → Login Theme → custom
```

seç ve Save.

<img width="849" height="796" alt="image" src="https://github.com/user-attachments/assets/f9990fe6-ba4f-4fa4-a6d3-a41c1fe11d8e" />

``` markdown
Cookie
Kerberos  
Identity Provider Redirector
browser-with-otp forms (sub-flow)
  ├── Username Password Form
  └── Türkçe OTP Doğrulama
```

<img width="1047" height="931" alt="image" src="https://github.com/user-attachments/assets/68e1ba45-e590-4b1a-b31a-e757b226ddbd" />

	http://localhost:9090/realms/master/account/

<img width="411" height="93" alt="image" src="https://github.com/user-attachments/assets/ac79bae9-943f-4620-838a-0a9083ee9162" />

<img width="916" height="342" alt="image" src="https://github.com/user-attachments/assets/543f2b34-819a-413b-bda9-3f540c144215" />


 


