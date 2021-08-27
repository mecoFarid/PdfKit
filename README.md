# PdfKit

## Get Started:

Add `jitpack` in your root `build.gradle` at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add PdfKit dependency

	dependencies {
	        implementation 'com.github.mecoFarid:PdfKit:v1.0'
	}

## Usage:

### 1. Convert local html string to PDF
    PdfKit(this).startConversion(
            baseUrl = getAssetDirectoryPath(),
            data = PdfConverterDataFactory.htmlData,
            javascriptEnabled = true,
            outputFile = outputFile,
            onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                override fun onError(e: Exception) {
                    println("PDFPRINT local onError: $e")
                }

                override fun onSuccess(pdfFileLocation: File) {
                    println("PDFPRINT local onSuccess: $outputFile")
                }
            }
    )

| Parameter     | Explanation   | Example
| ------------- | ------------- | ------------- |
| `baseUrl`     | Base URL where assets referenced in html string can be found by Android System  | `file:/storage/emulated/0/Android/data/com.mecofarid.pdfkitdemo/files/images/`
| `data`        | HTML string   | `<!DOCTYPE html><html><body>The body</body></html>`
| `outputFile`  | File where the pdf file should be saved must end with `.pdf` extension |  `/storage/emulated/0/Android/data/com.mecofarid.pdfkitdemo/files/pdf/PdfKitDemo_1.pdf`
| `javascriptEnabled`  | `true` if you want to enable javascript. By default it is `false` |  `-`

For example usage see [MainActivity](https://github.com/mecoFarid/PdfKit/blob/1913fb387d2fb48edfddee847d33951a14927b8b/app/src/main/java/com/mecofarid/pdfkitdemo/MainActivity.kt)

### 2. Load remote URL and convert it to PDF 

        getFile("PdfKitDemo_1.pdf").let { outputFile ->
            PdfKit(application.applicationContext).startConversion(
                    url = "https://www.github.com/",
                    outputFile = outputFile,
                    javascriptEnabled = true,
                    onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                        override fun onError(e: Exception) {
                            println("PDFPRINT remote onError: $e")
                        }

                        override fun onSuccess(pdfFileLocation: File) {
                            println("PDFPRINT remote onSuccess: $outputFile")
                        }
                    }
            )
        }
        
| Parameter     | Explanation   | Example
| ------------- | ------------- | ------------- |
| `url`         |  URL to be loaded to WebView  | `https://www.github.com/`
| `outputFile`  | File where the pdf file should be saved must end with `.pdf` extension |  `/storage/emulated/0/Android/data/com.mecofarid.pdfkitdemo/files/pdf/PdfKitDemo_1.pdf`
| `javascriptEnabled`  | `true` if you want to enable javascript. By default it is `false` |  `-`

For example usage see [MainActivity](https://github.com/mecoFarid/PdfKit/blob/1913fb387d2fb48edfddee847d33951a14927b8b/app/src/main/java/com/mecofarid/pdfkitdemo/MainActivity.kt)

## Troubleshooting

If you're getting following error when loading `http` schema URLs
> java.lang.Exception: Error loading `http://www.example.com/;` Error code: -1; Description: net::ERR_CLEARTEXT_NOT_PERMITTED; 

You can resolve this by adding [network security configuration](https://developer.android.com/training/articles/security-config#manifest). For example usage see 
[AndroidManifest.xml](https://github.com/mecoFarid/PdfKit/blob/master/app/src/main/AndroidManifest.xml) file of this project with `android:networkSecurityConfig="@xml/network_security_config"` aatribute

# Know Bug:
When loading `http`/`https` schema URLs, sometimes WebView's onPageFinished method won't be called due to Chromium bug 'crbug.com/1244039'.
