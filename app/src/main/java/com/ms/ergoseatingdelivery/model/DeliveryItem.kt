package com.ms.ergoseatingdelivery.model

import java.io.Serializable

data class DeliveryItem(
    var id: String,
    var deliveryDate: String,
    var from: String,
    var to: String,
    var clientName: String,
    var clientPhone: String,
    var clientEmail: String,
    var clientDistrict: String,
    var clientStreet: String,
    var clientBlock: String,
    var clientFloor: String,
    var clientUnit: String,
    var delivered: Boolean,
    var pdfURL: String): Serializable