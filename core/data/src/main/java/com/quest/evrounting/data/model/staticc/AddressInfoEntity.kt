package com.quest.evrounting.data.model.staticc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quest.evrounting.data.model.reference.RefCountryEntity

@Entity(
    tableName = "AddressInfo",
    // Định nghĩa các Foreign Keys để đảm bảo tính toàn vẹn dữ liệu
    foreignKeys = [ForeignKey(
        entity = RefCountryEntity::class,
        parentColumns = ["ID"],
        childColumns = ["CountryID"],
        onDelete = ForeignKey.Companion.RESTRICT, // Giống ON DELETE RESTRICT
        onUpdate = ForeignKey.Companion.CASCADE   // Giống ON UPDATE CASCADE
    )
    ],
    // Định nghĩa các Index để tăng tốc độ truy vấn
    indices = [
        Index(value = ["CountryID"]), // Tương ứng INDEX FkAddressInfoCountry
        Index(value = ["Geohash12"])  // Tương ứng INDEX IdxGeohash12
    ]
)
data class AddressInfoEntity(
    // autoGenerate = true tương ứng với AUTO_INCREMENT trong SQL.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int = 0, // Gán giá trị mặc định là 0 cho khóa chính tự tăng

    @ColumnInfo(name = "Title")
    val title: String,

    @ColumnInfo(name = "AddressLine1")
    val addressLine1: String?,

    @ColumnInfo(name = "Town")
    val town: String?,

    @ColumnInfo(name = "Postcode")
    val postcode: String?,

    @ColumnInfo(name = "CountryID")
    val countryId: Int,

    @ColumnInfo(name = "Latitude")
    val latitude: Double,

    @ColumnInfo(name = "Longitude")
    val longitude: Double,

    @ColumnInfo(name = "AccessComments")
    val accessComments: String?,

    // Geohash12 là BIGINT trong SQL, dùng Long trong Kotlin là chính xác.
    @ColumnInfo(name = "Geohash12")
    val geohash12: Long
)