<template>
    <div class="home">
        <DashBoard/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import DashBoard from '@/components/DashBoard.vue'

    export default {
        name: 'home',
        components: {
            DashBoard
        }
    }
</script>
