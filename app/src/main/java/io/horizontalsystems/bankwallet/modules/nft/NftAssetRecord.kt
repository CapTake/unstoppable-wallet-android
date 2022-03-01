package io.horizontalsystems.bankwallet.modules.nft

import androidx.room.Embedded
import androidx.room.Entity
import io.horizontalsystems.bankwallet.modules.hsnft.HsNftApiV1Response
import java.math.BigDecimal

@Entity(primaryKeys = ["accountId", "tokenId", "contract_address"])
data class NftAssetRecord(
    val accountId: String,
    val collectionUid: String,
    val tokenId: String,
    val name: String?,
    val imageUrl: String?,
    val imagePreviewUrl: String?,
    val description: String?,
    val onSale: Boolean,

    @Embedded
    val lastSale: NftAssetPrice?,

    @Embedded(prefix = "contract_")
    val contract: NftAssetContract,

    @Embedded
    val links: HsNftApiV1Response.Asset.Links?,

    val ownedCount: Int = 1,
)

data class NftAssetPrice(
    val coinTypeId: String,
    val value: BigDecimal
)

data class NftAssetContract(
    val address: String,
    val type: String
)