import os
import hashlib

# 1. ADIM: Bilinen virüs imzalarını (hash veya string) bir listeye ekleyelim.
# Bu, gerçek bir virüs imzası DEĞİL, örnek bir veridir.
bilinen_imzalar = [
    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", # örnek SHA-256
    "trojan_kotu_kod_parcasi", # örnek bir string
    "malware_belirtisi_xyz"
]

# 2. ADIM: Dosyayı tarayan fonksiyon
def dosya_tara(dosya_yolu):
    try:
        with open(dosya_yolu, 'rb') as dosya:
            dosya_verisi = dosya.read()

            # A) Dosyanın SHA-256 hash'ini al (bu, dosyanın parmak izidir)
            dosya_hash = hashlib.sha256(dosya_verisi).hexdigest()
            if dosya_hash in bilinen_imzalar:
                return f"🚨 UYARI: {dosya_yolu} dosyası bilinen bir zararlı yazılım imzası içeriyor! (Hash tespiti)"

            # B) Dosya içinde belirli bir metin parçasını ara (basit string arama)
            for imza in bilinen_imzalar:
                if imza.encode() in dosya_verisi: # imzayı byte'a çevirip arıyoruz
                    if len(imza) > 10: # çok kısa imzaları engelle (yanlış pozitif)
                        return f"🚨 UYARI: {dosya_yolu} dosyasında şüpheli kod parçası tespit edildi! ('{imza}')"

        return f"✅ TEMİZ: {dosya_yolu} dosyasında sorun tespit edilmedi."

    except Exception as hata:
        return f"❌ HATA: {dosya_yolu} taranamadı. Sebep: {hata}"

# 3. ADIM: Kullanıcıdan bir klasör veya dosya seçmesini iste (örnek)
taranacak_dosya = input("Tarayacağın dosyanın tam yolunu yaz (Örnek: C:\\kullanici\\belgeler\\test.exe): ")
sonuc = dosya_tara(taranacak_dosya)
print(sonuc)

# 4. ADIM: (Gelişmiş) Bir klasörü taramak için bu fonksiyonu klasördeki her dosya için çağır.
# Klasör tara: os.walk() kullan
def klasor_tara(klasor_yolu):
    for kok, klasorler, dosyalar in os.walk(klasor_yolu):
        for dosya in dosyalar:
            dosya_yolu = os.path.join(kok, dosya)
            sonuc = dosya_tara(dosya_yolu)
            print(sonuc)

# klasor_tara("C:\\TestKlasoru") # Örnek klasör taraması