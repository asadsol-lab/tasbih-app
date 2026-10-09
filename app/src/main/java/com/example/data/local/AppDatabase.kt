package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.SessionDao
import com.example.data.local.dao.ZikrDao
import com.example.data.local.entity.TasbihSessionEntity
import com.example.data.local.entity.ZikrEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ZikrEntity::class, TasbihSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun zikrDao(): ZikrDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val INITIAL_ZIKR_LIST = listOf(
            ZikrEntity(
                id = 1,
                name = "SubhanAllah",
                arabicText = "سُبْحَانَ اللَّهِ",
                urduTranslation = "اللہ پاک اور ہر عیب سے مبرا ہے",
                defaultTarget = 33,
                isCustom = false
            ),
            ZikrEntity(
                id = 2,
                name = "Alhamdulillah",
                arabicText = "الْحَمْدُ لِلَّهِ",
                urduTranslation = "تمام تعریفیں اور شکر اللہ تعالیٰ کے لیے ہیں",
                defaultTarget = 33,
                isCustom = false
            ),
            ZikrEntity(
                id = 3,
                name = "Allahu Akbar",
                arabicText = "اللَّهُ أَكْبَرُ",
                urduTranslation = "اللہ سب سے بڑا اور سب پر غالب ہے",
                defaultTarget = 34,
                isCustom = false
            ),
            ZikrEntity(
                id = 4,
                name = "La ilaha illallah",
                arabicText = "لَا إِلٰهَ إِلَّا اللَّهُ",
                urduTranslation = "اللہ کے سوا کوئی عبادت کے لائق نہیں",
                defaultTarget = 100,
                isCustom = false
            ),
            ZikrEntity(
                id = 5,
                name = "Astaghfirullah",
                arabicText = "أَسْتَغْفِرُ اللَّهَ",
                urduTranslation = "میں اپنے رب اللہ تعالیٰ سے مغفرت طلب کرتا ہوں",
                defaultTarget = 100,
                isCustom = false
            ),
            ZikrEntity(
                id = 6,
                name = "SubhanAllahi wa bihamdihi",
                arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
                urduTranslation = "اللہ پاک ہے اپنی تعریف کے ساتھ، اللہ پاک ہے عظمت والا",
                defaultTarget = 100,
                isCustom = false
            ),
            ZikrEntity(
                id = 7,
                name = "Durood Sharif (Salawat)",
                arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
                urduTranslation = "اے اللہ! رحمت نازل فرما محمدﷺ اور ان کی آل پر",
                defaultTarget = 100,
                isCustom = false
            ),
            ZikrEntity(
                id = 8,
                name = "Ayat-ul-Karima",
                arabicText = "لَا إِلٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                urduTranslation = "تیرے سوا کوئی معبود نہیں، تو پاک ہے، بے شک میں ہی خطا کار تھا",
                defaultTarget = 100,
                isCustom = false
            ),
            ZikrEntity(
                id = 9,
                name = "Hasbunallahu wa ni'mal wakeel",
                arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
                urduTranslation = "ہمیں اللہ کافی ہے اور وہ بہترین کارساز ہے",
                defaultTarget = 100,
                isCustom = false
            ),
            ZikrEntity(
                id = 10,
                name = "La hawla wa la quwwata",
                arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                urduTranslation = "برائی سے بچنے اور نیکی کرنے کی کوئی طاقت نہیں سوائے اللہ کی مدد کے",
                defaultTarget = 100,
                isCustom = false
            )
        )

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasbih_database.db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial authentic dhikr library
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.zikrDao()?.insertAll(INITIAL_ZIKR_LIST)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
