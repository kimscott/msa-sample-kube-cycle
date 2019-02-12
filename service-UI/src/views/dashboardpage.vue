<template>
    <div class="dashboardpage">
        <el-button @click="user=[{provider: 'All' , name: 'All'}]">Master</el-button>
        <el-button @click="user=[{provider: 'EC2' , name: 'nodes.jjy.k8s.local'}]">User 1</el-button>
        <el-button @click="user=[{provider: 'K8S' , name: 'test'}]">User 2</el-button>
        <el-button @click="user=[{provider: 'K8S' , name: 'default'}, {provider: 'EC2' , name: 'nodes.jjy.k8s.local'}]">User 3</el-button>
        <DashBoard :user="user"/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import DashBoard from '@/components/dashboard.vue'

    export default {
        name: 'home',
        data() {
            return {
                user: [{provider: 'All', name:'All'}]
            }
        },
        components: {
            DashBoard
        }
    }
</script>
