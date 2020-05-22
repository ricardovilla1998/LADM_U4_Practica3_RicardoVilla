package mx.edu.ittepic.ladm_u4_practica3_ricardovilla

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE ENTRANTES(ID INTEGER PRIMARY KEY AUTOINCREMENT,CELULAR VARCHAR(200),MENSAJE VARCHAR(2000),ENVIADO VARCHAR(5))")
        db.execSQL("CREATE TABLE FATALITIES(PERSONAJE VARCHAR(200),FATALITY VARCHAR(200))")
        db.execSQL("INSERT INTO FATALITIES VALUES('LIU_KANG','Bloqueo + Giro de 360 grados del Botón Direccional')")
        db.execSQL("INSERT INTO FATALITIES VALUES('JOHNNY_CAGE','Delante, Delante, Delante, Puñetazo Alto (Pegado al oponente)')")
        db.execSQL("INSERT INTO FATALITIES VALUES('SONYA','Delante, Delante, Atrás, Atrás, Bloqueo (A Media Distancia)')")
        db.execSQL("INSERT INTO FATALITIES VALUES('KANO','Atrás, Abajo, Delante, Puñetazo Bajo (Pegado al oponente)')")
        db.execSQL("INSERT INTO FATALITIES VALUES('SUB_ZERO','Delante, Abajo, Delante, Puñetazo Alto (Pegado al oponente)')")
        db.execSQL("INSERT INTO FATALITIES VALUES('SCORPION','Con Bloqueo Presionado, Arriba, Arriba (A Media Distancia)')")
        db.execSQL("INSERT INTO FATALITIES VALUES('RAIDEN','Delante, Atrás, Atrás, Atrás, Puñetazo Alto (Pegado al oponente)')")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}