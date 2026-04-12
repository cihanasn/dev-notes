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