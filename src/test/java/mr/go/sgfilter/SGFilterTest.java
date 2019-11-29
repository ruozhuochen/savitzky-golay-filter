package mr.go.sgfilter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class SGFilterTest {

    @Test
    void smooth() {
        List<Double> data = Arrays.asList(2.0, 4.0, 6.0, 2.0, 2.0, 2.0, 2.0, 3.0);
        int order = 1;
        int nl = data.size() / 2;
        int nr = data.size() - nl;
        double[] coefficients = SGFilter.computeSGCoefficients(nl, nr, order);
        double[] output = new SGFilter(nl, nr).smooth(data.stream().mapToDouble(Double::doubleValue).toArray(), coefficients);
        System.out.println(Arrays.stream(output).boxed().collect(Collectors.toList()));
    }
}
