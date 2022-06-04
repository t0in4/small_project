package com.eyehail.smallproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = getJWT()

        Log.i("JwtTest", a)
    }

    fun getJWT(): String {

        lateinit var privateKeyPem: PemObject
        var context: Context = this

        val file = context!!.assets.open("yapem.pem")
        //val file = File("app/assets/yapem.pem").inputStream()
        val isr = InputStreamReader(file)
        val readerBufferedFile = BufferedReader(isr)

        val reader: PemReader = PemReader(readerBufferedFile)
        privateKeyPem = reader.readPemObject()


        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKeyPem.getContent()))
        val serviceAccountId = "ajeijp19m599s8aos6vr"
        val keyId = "aje92dpc4t4tu7bbu8un"
        val now = Instant.now()

        //  JWT.
        val encodedToken: String = Jwts.builder()
            .setHeaderParam("kid", keyId)
            .setIssuer(serviceAccountId)
            .setAudience("https://iam.api.cloud.yandex.net/iam/v1/tokens")
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(360)))
            .signWith(
                privateKey,
                SignatureAlgorithm.PS256
            )
            .compact()
        Log.i("encoded Token", encodedToken)
        return encodedToken
    }
}