import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  getUrlPrefix: () => {}
}

const lavsourceJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceJsInterface