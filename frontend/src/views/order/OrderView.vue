<script setup>
import { ref, onMounted, computed } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import OrderCard from '@/components/cards/OrderCard.vue'
import orderApi from '@/api/orderApi'
import userApi from '@/api/userApi'
import router from '@/router'

const orders = ref([])
const role = ref('')
const user = ref({})
const dropdownVisible = ref(false)
const selectedOption = ref('All Orders')
const searchQuery = ref('')

const fetchOrders = async () => {
    try {
        const { data: orderResponse } = await orderApi.getOrders()
        const { data: userResponse } = await userApi.getUserByJwt()

        user.value = userResponse.data
        role.value = userResponse.data.role
        const loggedInUserId = user.value.id

        // Filter orders based on user role
        orders.value =
            role.value === 'ADMIN'
                ? orderResponse.data
                : orderResponse.data.filter(
                      (order) => order.user.id === loggedInUserId
                  )

        console.log('Filtered orders for display:', orders.value)
    } catch (error) {
        console.error('Error fetching orders:', error)
    }
}

onMounted(() => {
    fetchOrders()
})

const toggleDropdown = () => {
    dropdownVisible.value = !dropdownVisible.value
}

const selectOption = (option) => {
    selectedOption.value = option
    dropdownVisible.value = false
}

const filteredOrders = computed(() => {
    return orders.value.filter((order) => {
        const matchesStatus =
            selectedOption.value === 'All Orders' ||
            order.status === selectedOption.value
        const matchesSearch = searchQuery.value
            ? order.user.toLowerCase().includes(searchQuery.value.toLowerCase())
            : true
        return matchesStatus && matchesSearch
    })
})

const handleOrderSuccess = async (orderId) => {
    const order = orders.value.find((o) => o.id === orderId)
    if (order) {
        try {
            await orderApi.updateOrderStatus({ id: orderId, status: 'SUCCESS' })
            order.status = 'Success'
        } catch (error) {
            console.error('Error updating order:', error)
        }
    }
}

const handleViewDetail = (orderId) => {
    router.push({ name: 'receipt', params: { id: orderId } })
}

</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 h-screen"
        >
            <!-- Search section -->
            <!-- <section class="search">
                <Search @update-search="searchQuery = $event" />
            </section> -->

            <!-- Orders heading -->
            <section class="w-full">
                <span class="font-bold text-3xl">Order</span>
            </section>

            <!-- Type selection dropdown -->
            <div class="relative inline-block mt-2">
                <button
                    @click="toggleDropdown"
                    class="flex px-2 cursor-pointer gap-2 text-gray-500"
                >
                    {{ selectedOption }}
                    <fa icon="sort-down" />
                </button>
                <ul
                    v-if="dropdownVisible"
                    class="absolute bg-white rounded-lg shadow-lg text-gray-500"
                >
                    <li
                        v-for="option in [
                            'PENDING',
                            'COMPLETE',
                            'CANCEL',
                            'SUCCESS',
                            'All Orders',
                        ]"
                        :key="option"
                        @click="selectOption(option)"
                        class="px-4 py-2 hover:bg-gray-100 cursor-pointer rounded-lg"
                    >
                        {{ option }}
                    </li>
                </ul>
            </div>

            <!-- Order list -->
            <section class="mt-4">
                <OrderCard
                    v-for="(order, i) in filteredOrders"
                    :index="i + 1"
                    :key="order.id"
                    :order="order"
                    @mark-success="handleOrderSuccess"
                    @view-detail="handleViewDetail"
                />
            </section>
        </main>
    </div>
</template>