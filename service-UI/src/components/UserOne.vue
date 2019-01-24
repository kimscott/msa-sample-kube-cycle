<template>
    <div class="userone" style="margin: 10px">
        <h1> List </h1>
        <div>
            <dropdown :options="nameSpaceList"
                      :selected="selectedNamespace"
                      v-on:updateOption="methodToRunOnSelect"
                      :placeholder="'Select an Namespace'"

            >
            </dropdown>
        </div>
        <div class="table">
            <h2>Pods</h2>
            <vuetable ref="pods"
                      :fields="['Namespace', 'Name', 'statusType', 'phase', 'startTime', 'updateTime', 'endTime']"
                      :data="list"
            >
            </vuetable>
        </div>
        <!--<div class="table">-->
            <!--<h2>Services</h2>-->
            <!--<vuetable ref="services"-->
                      <!--:fields="['nameSpace', 'service', 'pod', 'deployment', 'replicaSet']"-->
                      <!--:data="list"-->
            <!--&gt;-->
            <!--</vuetable>-->
        <!--</div>-->
        <div class="table">
            <h2>Jobs</h2>
            <vuetable ref="jobs"
                      :fields="['nameSpace', 'service', 'pod', 'deployment', 'replicaSet']"
                      :data="list"
            >
            </vuetable>
        </div>
    </div>
</template>

<script>
    import VueTable from 'vuetable-2'
    import dropdown from 'vue-dropdowns';

    export default {

        name: 'UserOne',
        props: {
            msg: String,
        },
        components: {
            'vuetable': VueTable,
            'dropdown': dropdown,
        },
        data() {
            return {
                evtSource: null,
                nameSpaceList: [
                    {id: 1, name: 'One'},
                    {id: 2, name: 'Two'}
                ],
                selectedNamespace: {},
                list: []
            }
        },

        beforeDestroy: function () {
            if (this.evtSource) {
                console.log("closing evtSource beforeDestroy");
                this.evtSource.close();
            }
        },
        mounted() {
            if (this.selectedNamespace.name != undefined) {
                this.startSSE();
            }
        },
        watch: {
            selectedNamespace: function () {
                if (this.selectedNamespace.name != undefined) {
                    this.startSSE();
                }
            }
        },
        methods: {
            methodToRunOnSelect(payload) {
                this.selectedNamespace = payload;
            },
            startSSE: function () {
                var me = this;

                me.evtSource = new EventSource('http://localhost:8080/kubesse/' + me.selectedNamespace.name)
                me.evtSource.onmessage = function (e) {
                    // console.log(JSON.parse(e.data))
                    var parse = JSON.parse(e.data);
                    me.list.push(parse)
                }

                me.evtSource.onerror = function (e) {
                    if (me.evtSource) {
                        console.log("closing evtSource and reconnect");
                        me.evtSource.close();
                        me.startSSE();
                    }
                }
            }
        },
    }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
    h3 {
        margin: 40px 0 0;
    }
    .table {
        float: left;
        width: 45%;
        margin : 5px;
    }
    ul {
        list-style-type: none;
        padding: 0;
    }

    li {
        display: inline-block;
        margin: 0 10px;
    }

    a {
        color: #42b983;
    }
</style>
