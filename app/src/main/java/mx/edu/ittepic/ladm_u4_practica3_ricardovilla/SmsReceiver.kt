package mx.edu.ittepic.ladm_u4_practica3_ricardovilla

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast

class SmsReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        var extras = intent.extras
        var hilo = Hilo(MainActivity())
        var context_p : MainActivity ?= null
        //hilo.despausar()


        if(extras != null){
            //Obtener mensajes que vienen
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                //Obtener formato
                val formato = extras.getString("format")
                //Verifica versión que se está ejecutando
                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    //Mensaje
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)

                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                //GUARDAR SOBRE TABLA SQLITE
                try {
                    var baseDatos = BaseDatos(context,"ENTRANTES",null,1)
                    var insertar = baseDatos.writableDatabase
                    var SQL = "INSERT INTO ENTRANTES VALUES(NULL,'${celularOrigen}','${contenidoSMS}','0')"
                    insertar.execSQL(SQL)
                    baseDatos.close()

                }catch (error: SQLiteException){
                    Toast.makeText(context,error.message, Toast.LENGTH_LONG).show()
                }



                //Toast.makeText(context,"ENTRO CONTENIDO ${contenidoSMS}", Toast.LENGTH_LONG).show()
                //context_p!!.enviado = false

            }
        }
    }

}