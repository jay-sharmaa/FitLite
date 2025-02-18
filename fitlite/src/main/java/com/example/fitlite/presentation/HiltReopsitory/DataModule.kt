import android.content.Context
import com.example.fitlite.data.SensorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    fun provideHealthServices(@ApplicationContext context: Context): SensorRepository {
        return SensorRepository(context)
    }
}
