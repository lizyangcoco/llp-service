
class ListUtil {
    /**
     * adopt list to array
     * @param vars
     * @return
     */
    def list2Array(T vars) {
        def array = [] as T
        vars.each {
            if (it != '') {
                array.add(it)
            }
        }
        return array
    }

    /**
     * judge the list contain the element or not
     * @param list
     * @param element
     * @return
     */
    def isContainElement(list, element) {
        def result = false
        list.each {
            if (it == element) {
                result =  true
            }
        }
        return result
    }

    /**
     * return the index of element in list
     * @param list
     * @param element
     * @return
     */
    def findElementIndex(list, element) {
        if (!isContainElement(list, element)) {
            return -1
        }
        for (int i = 0; i < list.size(); i++) {
            if (list[i] == element) {
                return i
            }
        }
    }

    /**
     * return specific range of list
     * @param list
     * @param fromIndex
     * @param toIndex
     * @return
     */
    def getRangeList(list, fromIndex, toIndex) {
        return list[fromIndex..toIndex]
    }


}