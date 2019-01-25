<template>
    <div class="DashBoard" style="margin: 10px">
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

        name: 'DashBoard',
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
            var me = this
            this.startSSE();
            this.getNameSpace();
        },

        watch: {
            selectedNamespace: function (newVal) {
                var me = this;
                var name = newVal.name;
                if (name == 'All') {
                    this.$http.get(`${API_HOST}/kube/pod/`)
                        .then((result) => {
                            me.list = [];
                            me.list = result.data;
                        });
                    if (this.evtSource != null) {
                        this.evtSource.close();
                        me.startSSE();
                    }
                } else {
                    this.$http.get(`${API_HOST}/kube/pod/` + name)
                        .then((result) => {
                            me.list = [];
                            if (me.list.length == 0) {
                                result.data.forEach(function (data) {
                                    if (!(data.statusType == 'DELETED')) {
                                        me.list.push(data)
                                    }
                                })
                            }
                        });
                    if (this.evtSource != null) {
                        this.evtSource.close();
                        me.startSSE(name);
                    }
                }
            },
        },
        methods: {
            methodToRunOnSelect(payload) {
                this.selectedNamespace = payload;
            },
            getNameSpace: function () {
                var me = this;
                this.$http.get(`${API_HOST}/kube/pod/`)
                    .then((result) => {
                        // console.log(result);
                        if (me.list.length == 0) {
                            result.data.forEach(function (data) {
                                if (!(data.statusType == 'DELETED')) {
                                    me.list.push(data)
                                }
                            })
                        }
                        var i = 1;
                        var namespaceListTmp = [];
                        if (me.nameSpaceList.length > 0) {
                            me.nameSpaceList.forEach(function (podName) {
                                if (!namespaceListTmp.includes(podName.name)) {
                                    namespaceListTmp.push(podName.name)
                                }
                            })
                        }

                        result.data.forEach(function (pods) {
                            if (!namespaceListTmp.includes(pods.kubePodId.namespace)) {
                                namespaceListTmp.push(pods.kubePodId.namespace)
                                me.nameSpaceList.push({id: i, name: pods.kubePodId.namespace});
                                i++
                            }
                        })
                    })

            },
            startSSE: function (name) {
                var me = this;
                // this.getNameSpace();
                if (name == null) {
                    me.evtSource = new EventSource('http://localhost:8086/kubesse/')
                } else {
                    this.evtSource = new EventSource('http://localhost:8086/kubesse/?nameSpace=' + name)
                }
                var tmp = [];
                me.evtSource.onmessage = function (e) {
                    var parse = JSON.parse(e.data);
                    var parseMessage = JSON.parse(parse.message);
                    var listNameListTmp = [];
                    me.list.forEach(function (name) {
                        listNameListTmp.push(name.kubePodId.name)
                    });

                    me.list.some(function (listTmp, index) {
                        if (listTmp.kubePodId.name == parseMessage.kubePodId.name) {
                            console.log(me.list[index] + ':' + parseMessage);
                            me.list = [
                                ...me.list.slice(0, index),
                                parseMessage,
                                ...me.list.slice(index + 1)
                            ]
                            return;
                        } else if (!listNameListTmp.includes(parseMessage.kubePodId.name)) {
                            if (!(parseMessage.statusType == 'DELETED')) {
                                me.list.push(parseMessage)
                                listNameListTmp.push(parseMessage.kubePodId.name)
                                return;
                            }
                        }
                    })
                }
                me.evtSource.onerror = function (e) {
                    if (me.evtSource) {
                        console.log("closing evtSource and reconnect");
                        me.evtSource.close();
                        if (me.selectedNamespace.name != null) {
                            me.startSSE(me.selectedNamespace.name);
                        } else {
                            me.startSSE();
                        }
                    }
                }
            },
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
