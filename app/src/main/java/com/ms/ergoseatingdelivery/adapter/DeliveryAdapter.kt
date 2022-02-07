package com.ms.ergoseatingdelivery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ms.ergoseatingdelivery.R
import com.ms.ergoseatingdelivery.model.DeliveryItem

class DeliveryAdapter(
    private val context: Context,
    private val dataSource: List<DeliveryItem>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        // 1
        if (convertView == null) {

            // 2
            view = inflater.inflate(R.layout.list_item_delivery, parent, false)

            // 3
            holder = ViewHolder()
            holder.tvDeliveryDate = view.findViewById(R.id.tv_delivery_date) as TextView
            holder.tvTime = view.findViewById(R.id.tv_time) as TextView
            holder.tvClientName = view.findViewById(R.id.tv_client_name) as TextView
            holder.tvPhoneNumber = view.findViewById(R.id.tv_phone_number) as TextView
            holder.tvAddress = view.findViewById(R.id.tv_address) as TextView
            holder.ivCheck = view.findViewById(R.id.iv_check) as ImageView

            // 4
            view.tag = holder
        } else {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // 6
        val tvDeliveryDate = holder.tvDeliveryDate
        val tvTime = holder.tvTime
        val tvClientName = holder.tvClientName
        val tvPhoneNumber = holder.tvPhoneNumber
        val tvAddress = holder.tvAddress
        val ivCheck = holder.ivCheck


        val deliveryItem = getItem(position) as DeliveryItem

        tvDeliveryDate.text = deliveryItem.deliveryDate
        tvTime.text = deliveryItem.from + " ~ " +deliveryItem.to
        tvClientName.text = deliveryItem.clientName
        tvPhoneNumber.text = deliveryItem.clientPhone
        val address = deliveryItem.clientUnit + " " +
                deliveryItem.clientFloor + " " +
                deliveryItem.clientBlock + " " +
                deliveryItem.clientDistrict
        tvAddress.text = address
        when {
            deliveryItem.delivered -> ivCheck.visibility = View.VISIBLE
            else -> ivCheck.visibility = View.INVISIBLE
        }
        return view
    }

    private class ViewHolder {
        lateinit var tvDeliveryDate: TextView
        lateinit var tvTime: TextView
        lateinit var tvClientName: TextView
        lateinit var tvPhoneNumber: TextView
        lateinit var tvAddress: TextView
        lateinit var ivCheck: ImageView
    }
}