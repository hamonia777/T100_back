package org.example.t100.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.test.dto.TestRequestDto;
import org.example.t100.domain.test.dto.TestResponseDto;
import org.example.t100.domain.test.entity.Test;
import org.example.t100.domain.test.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    public List<TestResponseDto> getTest()
    {
        List<Test> testList = testRepository.findAll();
        List<TestResponseDto> testResponseDtoList = new ArrayList<>();
        for(Test test : testList)
        {
            TestResponseDto  testResponseDto = new TestResponseDto(test.getId(),test.getData(),test.getCreatedAt());
            testResponseDtoList.add(testResponseDto);
        }
        return testResponseDtoList;
    }
    public void setTest(TestRequestDto testRequestDto)
    {
        Test test = new Test();
        test.setData(testRequestDto.getData());
        testRepository.save(test);
    }
}
