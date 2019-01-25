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
                      :fields="['kubePodId.namespace', 'kubePodId.name', 'statusType', 'phase', 'startTime', 'updateTime', 'endTime']"
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
                      :fields="['kubePodId.namespace', 'kubePodId.name', 'statusType', 'phase', 'startTime', 'updateTime', 'endTime']"
                      :data="list2"
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
                nameSpaceList: [{id: 0, name: 'All'}],
                selectedNamespace: {},
                list: [],
                list2: []
            }
        },

        beforeDestroy: function () {
            if (this.evtSource) {
                console.log("closing evtSource beforeDestroy");
                this.evtSource.close();
            }
        },
        mounted() {
            this.startSSE();
            var me = this
            this.$http.get(`${API_HOST}/kube/pod/`)
                .then((result) => {
                    // console.log(result);
                    me.list = result.data;
                    var i = 1;
                    var namespaceListTmp = [];

                    result.data.forEach(function (pods) {
                        if (!namespaceListTmp.includes(pods.kubePodId.namespace)) {
                            namespaceListTmp.push(pods.kubePodId.namespace)
                            me.nameSpaceList.push({id: i, name: pods.kubePodId.namespace});
                            i++
                        }
                    })

                })
        },

        watch: {
            selectedNamespace: function (newVal) {
                var me = this;
                var name = newVal.name
                if(newVal.name == 'All') {
                    if (this.evtSource != null) {
                        this.evtSource.close();
                        this.evtSource = new EventSource('http://localhost:8086/kubesse/')
                    }
                } else if (this.evtSource != null) {
                    this.evtSource.close();
                    this.evtSource = new EventSource('http://localhost:8086/kubesse/?nameSpace=' + name)
                };
                this.$http.get(`${API_HOST}/kube/pod/` + name)
                    .then((result) => {
                        me.list = [];
                        me.list = result.data;
                    });
            },
        },
        methods: {
            methodToRunOnSelect(payload) {
                this.selectedNamespace = payload;
            },
            startSSE: function () {
                var me = this;

                me.evtSource = new EventSource('http://localhost:8086/kubesse/')
                var tmp = [];
                me.evtSource.onmessage = function (e) {
                    var parse = JSON.parse(e.data);
                    var parseMessage = JSON.parse(parse.message);
                    me.list.forEach(function (list, index) {
                        console.log(list)
                    });
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
        margin: 5px;
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
