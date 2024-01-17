const emptyImplementation = () => () => androidInterfaceWarning()

function androidInterfaceWarning() {
  console.warn('You are calling a Android JavaScript Interface function directly in browser!')
}

const androidInterfaces = {
  basicJsInterface: window['android_BasicJsInterface'] ?? {
    openNewWebActivity: path => {
      androidInterfaceWarning()
      window.location.href = path
    },
    finishCurrentWebActivity: () => {
      androidInterfaceWarning()
      history.back()
    }
  },
  lavsourceServerJsInterface: window['android_LavsourceServerJsInterface'] ?? {
    getUrlPrefix: emptyImplementation()
  }
}

export default androidInterfaces