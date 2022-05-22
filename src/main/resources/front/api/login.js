function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

function getMessage(data) {
   return $axios({
        'url': '/user/getCode',
        'method': 'post',
        data
    })
}




  