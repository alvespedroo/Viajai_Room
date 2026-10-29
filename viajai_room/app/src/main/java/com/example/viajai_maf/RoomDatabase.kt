package com.example.viajai_maf

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.OnConflictStrategy
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity(tableName = "viagens")
data class ViagemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val origem: String,
    val destino: String,
    val data: String,
    val orcamento: Double,
    val concluida: Boolean = false
)

@Entity(tableName = "itens_checklist")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoria: String,
    val nome: String,
    val marcado: Boolean = false,
    val viagemId: Int
)

@Dao
interface ViagemDao {
    @Insert suspend fun inserir(viagem: ViagemEntity): Long
    @Query("SELECT * FROM viagens ORDER BY id DESC") suspend fun buscarTodos(): List<ViagemEntity>
    @Query("SELECT * FROM viagens WHERE id = :id LIMIT 1") suspend fun buscarPorId(id: Int): ViagemEntity?
    @Update suspend fun atualizar(viagem: ViagemEntity)
    @Delete suspend fun deletar(viagem: ViagemEntity)
    @Query("SELECT * FROM viagens WHERE concluida = :concluida ORDER BY id DESC") suspend fun buscarPorStatus(concluida: Boolean): List<ViagemEntity>
}

@Dao
interface ChecklistDao {
    @Insert suspend fun inserir(item: ChecklistEntity): Long
    @Query("SELECT * FROM itens_checklist ORDER BY id ASC") suspend fun buscarTodos(): List<ChecklistEntity>
    @Query("SELECT * FROM itens_checklist WHERE id = :id LIMIT 1") suspend fun buscarPorId(id: Int): ChecklistEntity?
    @Update suspend fun atualizar(item: ChecklistEntity)
    @Delete suspend fun deletar(item: ChecklistEntity)
    @Query("SELECT * FROM itens_checklist WHERE viagemId = :viagemId ORDER BY id ASC") suspend fun buscarPorViagem(viagemId: Int): List<ChecklistEntity>
    @Query("DELETE FROM itens_checklist WHERE viagemId = :viagemId AND categoria = :categoria") suspend fun deletarCategoria(viagemId: Int, categoria: String)
}


@Entity(tableName = "itens_bagagem")
data class BagagemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val quantidade: Int,
    val preparado: Boolean = false
)

@Entity(tableName = "despesas")
data class DespesaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descricao: String,
    val categoria: String,
    val valor: Double,
    val viagemId: Int
)

@Entity(tableName = "lugares_salvos")
data class LugarSalvoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lugarId: Int
)

@Dao
interface BagagemDao {
    @Insert suspend fun inserir(item: BagagemEntity): Long
    @Query("SELECT * FROM itens_bagagem ORDER BY id ASC") suspend fun buscarTodos(): List<BagagemEntity>
    @Query("SELECT * FROM itens_bagagem WHERE id = :id LIMIT 1") suspend fun buscarPorId(id: Int): BagagemEntity?
    @Update suspend fun atualizar(item: BagagemEntity)
    @Delete suspend fun deletar(item: BagagemEntity)
}

@Dao
interface DespesaDao {
    @Insert suspend fun inserir(despesa: DespesaEntity): Long
    @Query("SELECT * FROM despesas ORDER BY id DESC") suspend fun buscarTodos(): List<DespesaEntity>
    @Query("SELECT * FROM despesas WHERE id = :id LIMIT 1") suspend fun buscarPorId(id: Int): DespesaEntity?
    @Update suspend fun atualizar(despesa: DespesaEntity)
    @Delete suspend fun deletar(despesa: DespesaEntity)
    @Query("SELECT * FROM despesas WHERE viagemId = :viagemId ORDER BY id DESC") suspend fun buscarPorViagem(viagemId: Int): List<DespesaEntity>
}

@Dao
interface LugarSalvoDao {
    @Insert suspend fun inserir(lugar: LugarSalvoEntity): Long
    @Query("SELECT * FROM lugares_salvos ORDER BY id ASC") suspend fun buscarTodos(): List<LugarSalvoEntity>
    @Query("SELECT * FROM lugares_salvos WHERE lugarId = :lugarId LIMIT 1") suspend fun buscarPorLugarId(lugarId: Int): LugarSalvoEntity?
    @Update suspend fun atualizar(lugar: LugarSalvoEntity)
    @Delete suspend fun deletar(lugar: LugarSalvoEntity)
}


@Entity(tableName = "avaliacoes")
data class AvaliacaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lugarId: Int,
    val autor: String,
    val texto: String,
    val nota: Double,
    val curtidas: Int = 0,
    val curtiu: Boolean = false,
    val isMinha: Boolean = false
)

@Dao
interface AvaliacaoDao {
    @Insert suspend fun inserir(avaliacao: AvaliacaoEntity): Long
    @Query("SELECT * FROM avaliacoes ORDER BY id DESC") suspend fun buscarTodos(): List<AvaliacaoEntity>
    @Query("SELECT * FROM avaliacoes WHERE lugarId = :lugarId ORDER BY id DESC") suspend fun buscarPorLugar(lugarId: Int): List<AvaliacaoEntity>
    @Query("SELECT * FROM avaliacoes ORDER BY id DESC") suspend fun buscarTodas(): List<AvaliacaoEntity>
    @Query("SELECT * FROM avaliacoes WHERE isMinha = 1 ORDER BY id DESC") suspend fun buscarMinhas(): List<AvaliacaoEntity>
    @Query("UPDATE avaliacoes SET autor = :novoNome WHERE isMinha = 1") suspend fun atualizarAutorMinhas(novoNome: String)
    @Query("SELECT * FROM avaliacoes WHERE id = :id LIMIT 1") suspend fun buscarPorId(id: Int): AvaliacaoEntity?
    @Update suspend fun atualizar(avaliacao: AvaliacaoEntity)
    @Delete suspend fun deletar(avaliacao: AvaliacaoEntity)
}

@Entity(tableName = "perfil_usuario")
data class PerfilUsuarioEntity(
    @PrimaryKey val id: Int = 1,
    val nome: String
)

@Dao
interface PerfilUsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun salvar(perfil: PerfilUsuarioEntity)
    @Query("SELECT * FROM perfil_usuario WHERE id = 1 LIMIT 1") suspend fun buscar(): PerfilUsuarioEntity?
}

@Database(entities = [ViagemEntity::class, ChecklistEntity::class, BagagemEntity::class, DespesaEntity::class, LugarSalvoEntity::class, AvaliacaoEntity::class, PerfilUsuarioEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun viagemDao(): ViagemDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun bagagemDao(): BagagemDao
    abstract fun despesaDao(): DespesaDao
    abstract fun lugarSalvoDao(): LugarSalvoDao
    abstract fun avaliacaoDao(): AvaliacaoDao
    abstract fun perfilUsuarioDao(): PerfilUsuarioDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS avaliacoes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, lugarId INTEGER NOT NULL, autor TEXT NOT NULL, texto TEXT NOT NULL, nota REAL NOT NULL, curtidas INTEGER NOT NULL, curtiu INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS perfil_usuario (id INTEGER NOT NULL, nome TEXT NOT NULL, PRIMARY KEY(id))")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE avaliacoes ADD COLUMN isMinha INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE avaliacoes SET isMinha = 1 WHERE autor IN (SELECT nome FROM perfil_usuario)")
            }
        }
        fun getDatabase(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "viajai_database")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { INSTANCE = it }
        }
    }
}
