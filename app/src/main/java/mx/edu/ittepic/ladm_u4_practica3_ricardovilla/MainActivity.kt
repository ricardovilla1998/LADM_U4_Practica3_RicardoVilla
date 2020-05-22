package mx.edu.ittepic.ladm_u4_practica3_ricardovilla

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val siPermiso = 1
    val siPermisoReceiver = 2
    val siPermisoLectura = 3
    var enviado = true
    var hilo = Hilo(this)
    var cadena =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS), siPermisoReceiver
            )

        }







        hilo.start()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Pregunta si ya existe el permiso para mandarlo directamente a la función
        if(requestCode == siPermiso){

        }

        if(requestCode == siPermisoReceiver){



           // mensajeRecibir()



        }

        if(requestCode == siPermisoLectura){


        }


    }


    private fun mensajeRecibir() {


        AlertDialog.Builder(this)
            .setMessage("SE OTORGO RECIBIR")
            .show()
    }

    fun compruebaMensaje(m:String):Boolean{

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_SMS), siPermisoLectura
            )

        }

        try{


            //DIVIDIR MENSAJE
            var fatality = m.split("\\s".toRegex())[0]
            var personaje = m.split("\\s".toRegex())[1]

            if(m.split("\\s".toRegex()).size > 2){
                return false
            }

            if(fatality == "FATALITY" && personaje.toUpperCase()!=""){
                return true
            }


        }catch (e:IndexOutOfBoundsException){
            return false
        }
        return false
    }

    fun buscarFatality(personaje:String):String{
        var fatal = ""
        try {
            var baseDatos = BaseDatos(this,"ENTRANTES",null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT FATALITY FROM FATALITIES WHERE PERSONAJE = ?"
            var parametros = arrayOf(personaje)

            var cursor = select.rawQuery(SQL,parametros)

            //PREGUNTA SI HAY TUPLAS
            if(cursor.moveToFirst()){

                //SI HAY RESULTADO
                fatal = cursor.getString(0)
            }
            else{
                fatal = "NO HAY DATA DE ESTE PERSONAJE, INTENTE CON OTRO"
            }
            select.close()
            baseDatos.close()



        }catch (error:SQLiteException){

        }

        return fatal

    }

    fun enviarSMS() {

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //Solicitar permisos
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS),siPermiso)
        }



        //enviado = false
        var fatality = ""
        var personaje = ""



        try {
            val cursor = BaseDatos(this,"ENTRANTES",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM ENTRANTES",null)

            var id = ""
            var ultimoNum = ""
            var ultimoMsj = ""
            var estado = ""



            if(cursor.moveToFirst()) {

                do {
                    //id = cursor.getString(0)
                    ultimoNum = cursor.getString(1)
                    ultimoMsj = cursor.getString(2)
                    estado = cursor.getString(3)





                    if (compruebaMensaje(ultimoMsj)) {


                        personaje = ultimoMsj.split("\\s".toRegex())[1]
                        cadena = buscarFatality(personaje.toUpperCase())


                        if (estado == "0") {

                            SmsManager.getDefault().sendTextMessage(ultimoNum, null, cadena, null, null)


                            Toast.makeText(
                                this,
                                "MENSAJE ENVIADO A ${ultimoNum}: " + cadena,
                                Toast.LENGTH_LONG
                            )
                                .show()
                            //txt_enviado.setText(cadena)
                            actualizaEstado(ultimoNum)
                           // hilo.pausar()
                            //hilo.despausar()


                        } else {
                            // Toast.makeText(this, "YA SE HA MANDADO", Toast.LENGTH_LONG) .show()

                        }

                    } else {

                        if (estado == "0") {
                            cadena = "ERROR, LA SINTAXIS DEBE SER:FATALITY NOMBREPERSONAJE"
                            SmsManager.getDefault().sendTextMessage(ultimoNum, null, cadena, null, null)
                            Toast.makeText(
                                this,
                                "MENSAJE ENVIADO A ${ultimoNum}: " + cadena,
                                Toast.LENGTH_LONG
                            )

                            actualizaEstado(ultimoNum)
                            //hilo.pausar()
                            //hilo.despausar()


                        }

                        //editText_msjEnviado.setText(fatality)

                    }



                }while(cursor.moveToNext())
            }
            else{

            }



            cursor.close()

            //Toast.makeText(this,fatality+" "+personaje,Toast.LENGTH_LONG).show()
            editText_num.setText(ultimoNum)
            editText_msj.setText(ultimoMsj)






            //hilo.pausar()

        }catch (error: SQLiteException){
            Toast.makeText(this,error.message,Toast.LENGTH_LONG).show()
        }




    }
    fun actualizaEstado (tel:String){

        // 0 -> No respondido
        // 1 -> Respondido
        try{
            var baseDatos = BaseDatos(this,"ENTRANTES",null,1)

            var insertar = baseDatos.writableDatabase
            var SQL = "UPDATE ENTRANTES SET ENVIADO ='1' WHERE CELULAR = ?"
            var parametros = arrayOf(tel)
            insertar.execSQL(SQL,parametros)
            //mensaje("SE ACTUALIZO CORRECTAMENTE")
            insertar.close()
            baseDatos.close()


        }catch (error:SQLiteException){
            Toast.makeText(this,error.message,Toast.LENGTH_LONG).show()
        }
    }



}
//CLASE HILO
class Hilo (p:MainActivity) : Thread(){
    private var iniciado = false
    private var puntero = p
    private var pausa = false

    override fun run() {
        super.run()
        iniciado = true
        while(iniciado){
            sleep(1000)

            if(!pausa){
                puntero.runOnUiThread {

                    puntero.compruebaMensaje("")
                    puntero.enviarSMS()

                }
            }
        }

    }

    fun estaIniciado(): Boolean {
        return iniciado
    }

    fun pausar() {
        pausa = true
    }

    fun despausar() {
        pausa = false
    }

    fun detener() {
        iniciado = false
    }
}

