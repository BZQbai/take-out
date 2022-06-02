package com.itheima.reggie.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.domain.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class TestEmployee {

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testPage(){
        IPage<Employee> pg = new Page<>(1,5);
        IPage<Employee> page = employeeService.page(pg);
        System.out.println(page);
    }


    @Test
    public void testGetCode(){
        Integer integer = ValidateCodeUtils.generateValidateCode(4);
        System.out.println(integer);

    }


    @Test
    public void containsNearbyDuplicate() {
        int[] nums = {1, 0, 1, 4, 1};
        int k = 1;
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < nums.length; i++) {
            if(set.add(nums[i])) {
                if(set.size() > k) {
                    set.remove(nums[i - k]);
                }
            } else {
               // return true;
                System.out.println(1);
            }
        }
        //return false;
        System.out.println(2);
    }
}
