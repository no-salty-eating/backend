package com.study.payment.infrastructure.client.response

data class PaymentResponse(
    val mId: String,
    val lastTransactionKey: String,
    val paymentKey: String,
    val orderId: String,
    val orderName: String,
    val taxExemptionAmount: Int,
    val status: String,
    val requestedAt: String, // ISO 8601 형식의 문자열
    val approvedAt: String, // ISO 8601 형식의 문자열
    val useEscrow: Boolean,
    val cultureExpense: Boolean,
    val card: Card?,
    val virtualAccount: Any?, // null 가능
    val transfer: Any?, // null 가능
    val mobilePhone: Any?, // null 가능
    val giftCertificate: Any?, // null 가능
    val cashReceipt: Any?, // null 가능
    val cashReceipts: Any?, // null 가능
    val discount: Any?, // null 가능
    val cancels: Any?, // null 가능
    val secret: Any?, // null 가능
    val type: String,
    val easyPay: EasyPay?,
    val country: String,
    val failure: Any?, // null 가능
    val isPartialCancelable: Boolean,
    val receipt: Receipt?,
    val checkout: Checkout?,
    val currency: String,
    val totalAmount: Int,
    val balanceAmount: Int,
    val suppliedAmount: Int,
    val vat: Int,
    val taxFreeAmount: Int,
    val metadata: Any?, // null 가능
    val method: String,
    val version: String
)

data class Card(
    val issuerCode: String?,
    val acquirerCode: String?,
    val number: String?,
    val installmentPlanMonths: Int?,
    val isInterestFree: Boolean?,
    val interestPayer: String?,
    val approveNo: String?,
    val useCardPoint: Boolean?,
    val cardType: String?,
    val ownerType: String?,
    val acquireStatus: String?,
    val receiptUrl: String?,
    val amount: Int?
)

data class EasyPay(
    val provider: String?,
    val amount: Int?,
    val discountAmount: Int?
)

data class Receipt(
    val url: String?
)

data class Checkout(
    val url: String?
)
