<template>
    <div>
      <h3>Result:</h3>
      <pre>{{ data }}</pre>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      data: {
        type: [Object, String],
        required: true
      }
    }
  }
  </script>
  