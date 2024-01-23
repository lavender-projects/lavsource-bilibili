import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  getUrlPrefix: jsInterfaceUtils.emptyImplementation()
}

const lavsourceServerJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceServerJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceServerJsInterface