<template>
    <div class="home">
        <UserOne/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import UserOne from '@/components/UserOne.vue'

    export default {
        name: 'home',
        components: {
            UserOne
        }
    }
</script>
