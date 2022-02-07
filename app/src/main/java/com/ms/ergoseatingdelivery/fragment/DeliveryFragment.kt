package com.ms.ergoseatingdelivery.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ms.ergoseatingdelivery.*
import com.ms.ergoseatingdelivery.adapter.DeliveryAdapter
import com.ms.ergoseatingdelivery.api.ApiInterface
import com.ms.ergoseatingdelivery.model.DeliveryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var listViewDeliveries: ListView
    lateinit var myAdapter: DeliveryAdapter
    lateinit var loadingDialog: LoadingDialog

    var deliveryList: ArrayList<DeliveryItem> = ArrayList()
    lateinit var productType: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_delivery, container, false)
        initView(view)
        return view
    }

    private fun initView(v: View) {
        listViewDeliveries = v.findViewById(R.id.listview_deliveries)
        myAdapter = DeliveryAdapter(requireActivity(), deliveryList)
        listViewDeliveries.adapter = myAdapter
        listViewDeliveries.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = deliveryList[position]
            startDeliveryDetailActivity(selectedItem)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadDeliveryList()
    }

    fun loadDeliveryList() {
        deliveryList.clear()

        loadingDialog.show()
        val token = "Bearer " + Preferences(requireContext()).token
        val apiInterface = ApiInterface.create().getDeliveryList(token, productType, mainActivity.fromDate, mainActivity.toDate)
        apiInterface.enqueue(object: Callback<List<DeliveryItem>> {
            override fun onResponse(
                call: Call<List<DeliveryItem>>,
                response: Response<List<DeliveryItem>>
            ) {
                loadingDialog.dismiss()
                val res = response.body()
                if (res != null) {
                    deliveryList.addAll(res)
                }
                myAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<DeliveryItem>>, t: Throwable) {
                loadingDialog.dismiss()
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    private var activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data != null) {
                val deliveryItem = data.getSerializableExtra(DeliveryDetailActivity.EXTRA_DELIVERY_DATA) as DeliveryItem
                replaceDeliveryListItem(deliveryItem)
                myAdapter.notifyDataSetChanged()
            }

        }
    }

    private fun startDeliveryDetailActivity(item: DeliveryItem) {
        val intent = DeliveryDetailActivity.newIntent(mainActivity, item, productType)
//        activityForResult.launch(intent)
        startActivity(intent)
    }

    private fun replaceDeliveryListItem(item: DeliveryItem) {
        for (deliveryItem in deliveryList) {
            if(deliveryItem.id == item.id) {
                val nIdx = deliveryList.indexOf(deliveryItem)
                deliveryList[nIdx] = item
                return
            }
        }
    }
}
