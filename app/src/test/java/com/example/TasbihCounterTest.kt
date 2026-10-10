package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.TasbihSessionEntity
import com.example.data.local.entity.ZikrEntity
import com.example.ui.home.HomeUiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TasbihCounterTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testIncrementLogic() {
        var state = HomeUiState(count = 0, target = 33)
        // Increment
        state = state.copy(count = state.count + 1)
        assertEquals(1, state.count)
        assertEquals(32, state.remaining)
        assertFalse(state.isTargetCompleted)

        // Increment up to target
        state = state.copy(count = 33)
        assertEquals(33, state.count)
        assertEquals(0, state.remaining)
        assertEquals(1.0f, state.progress, 0.001f)
    }

    @Test
    fun testUndoCannotGoNegative() {
        var state = HomeUiState(count = 0, target = 33)
        val newCount = maxOf(0, state.count - 1)
        assertEquals(0, newCount)

        state = state.copy(count = 5)
        val undone = maxOf(0, state.count - 1)
        assertEquals(4, undone)
    }

    @Test
    fun testTargetValidation() {
        val validTarget = 99
        val invalidZero = 0
        val invalidNegative = -5
        val invalidExcessive = 1_000_000

        assertTrue(validTarget in 1..999999)
        assertFalse(invalidZero in 1..999999)
        assertFalse(invalidNegative in 1..999999)
        assertFalse(invalidExcessive in 1..999999)
    }

    @Test
    fun testDuplicateCompletionPrevention() {
        val target = 33
        var completedAt: Long? = null

        fun onIncrement(newCount: Int) {
            if (newCount >= target && completedAt == null) {
                completedAt = 123456789L
            }
        }

        onIncrement(32)
        assertEquals(null, completedAt)

        onIncrement(33)
        assertEquals(123456789L, completedAt)

        // Continuing beyond target does NOT overwrite completedAt
        onIncrement(34)
        assertEquals(123456789L, completedAt)
    }

    @Test
    fun testRoomZikrAndSessionPersistence() = runBlocking {
        val zikrDao = database.zikrDao()
        val sessionDao = database.sessionDao()

        val zikr = ZikrEntity(
            name = "SubhanAllah",
            arabicText = "سُبْحَانَ اللَّهِ",
            urduTranslation = "اللہ پاک ہے",
            defaultTarget = 33,
            isCustom = false
        )
        val zikrId = zikrDao.insertZikr(zikr)

        val retrievedZikr = zikrDao.getZikrByIdSync(zikrId)
        assertNotNull(retrievedZikr)
        assertEquals("SubhanAllah", retrievedZikr?.name)

        // Session with snapshot name
        val session = TasbihSessionEntity(
            zikrId = zikrId,
            zikrNameSnapshot = retrievedZikr!!.name,
            count = 33,
            target = 33,
            status = "COMPLETED"
        )
        val sessionId = sessionDao.insertSession(session)

        val retrievedSession = sessionDao.getSessionById(sessionId).first()
        assertNotNull(retrievedSession)
        assertEquals(33, retrievedSession?.count)
        assertEquals("SubhanAllah", retrievedSession?.zikrNameSnapshot)

        // Even if custom zikr was deleted, session snapshot remains preserved
        zikrDao.deleteZikrById(zikrId)
        val historyAfterDelete = sessionDao.getAllSessions().first()
        assertEquals(1, historyAfterDelete.size)
        assertEquals("SubhanAllah", historyAfterDelete.first().zikrNameSnapshot)
    }

    @Test
    fun testStatisticsCalculation() = runBlocking {
        val sessionDao = database.sessionDao()

        val now = System.currentTimeMillis()
        val s1 = TasbihSessionEntity(
            zikrNameSnapshot = "SubhanAllah",
            count = 33,
            target = 33,
            startedAt = now,
            status = "COMPLETED"
        )
        val s2 = TasbihSessionEntity(
            zikrNameSnapshot = "Alhamdulillah",
            count = 33,
            target = 33,
            startedAt = now,
            status = "COMPLETED"
        )
        val s3 = TasbihSessionEntity(
            zikrNameSnapshot = "Allahu Akbar",
            count = 10,
            target = 34,
            startedAt = now,
            status = "PAUSED"
        )

        sessionDao.insertSession(s1)
        sessionDao.insertSession(s2)
        sessionDao.insertSession(s3)

        val totalAllTime = sessionDao.getTotalCountAllTime().first()
        assertEquals(76, totalAllTime)

        val completedCount = sessionDao.getCompletedSessionsCount().first()
        assertEquals(2, completedCount)
    }

    @Test
    fun testDeviceGuideDetection() {
        val guide = com.example.util.DeviceUtils.getDeviceGuide()
        assertNotNull(guide)
        assertTrue(guide.stepsUrdu.isNotEmpty())
        assertTrue(guide.stepsEnglish.isNotEmpty())
    }

    @Test
    fun testIslamicTuneAvailability() {
        val tunes = com.example.util.IslamicTunePlayer.AVAILABLE_TUNES
        assertTrue(tunes.size >= 4)
        assertNotNull(tunes.firstOrNull { it.id == "azan_makkah" })
        assertNotNull(tunes.firstOrNull { it.id == "azan_fajr" })
    }
}
