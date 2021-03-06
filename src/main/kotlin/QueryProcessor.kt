import com.bpodgursky.jbool_expressions.*
import com.bpodgursky.jbool_expressions.parsers.ExprParser
import com.bpodgursky.jbool_expressions.rules.RuleSet


class QueryException(override val message: String): Exception(message)


class QueryProcessor(
    private val index: Index,
    private val ranker: BertRanker
) {
    fun query(rawQuery: String): List<Pair<String, Double>> {
        return ranker.rank(rawQuery, index.translateToDocNames(processQuery(rawQuery)))
    }

    fun processQuery(rawQuery: String): Set<Int> {
        val query = ExprParser.parse(rawQuery)
        val dnfQuery = RuleSet.toDNF(query)

        if (dnfQuery.exprType == "literal") {
            return if (!(dnfQuery as Literal<String>).value) {
                emptySet()
            } else {
                index.getALlDocIds()
            }
        }
        return processDnf(dnfQuery)
    }

    private fun processVariable(variable: Variable<String>): Set<Int> {
        return index.getRelatedDocIds(variable.value)
    }

    private fun processNot(notExpr: Not<String>): Set<Int> {
        return index.getUnrelatedDocIds((notExpr.e as Variable<String>).value)
    }

    private fun processLiteral(literal: Expression<String>): Set<Int> {
        return when (literal.exprType) {
            "variable" -> {
                processVariable(literal as Variable<String>)
            }
            "not" -> {
                processNot(literal as Not<String>)
            }
            else -> {
                throw QueryException("Can not process literal with type: '${literal.exprType}'")
            }
        }
    }

    private fun processAnd(andExpr: And<String>): Set<Int> {
        var result = processLiteral(andExpr.children[0])
        andExpr.children.drop(1).forEach { child ->
            result = result.intersect(processLiteral(child))
        }
        return result
    }

    private fun processOr(orExpr: Or<String>): Set<Int> {
        var result = processLiteral(orExpr.children[0])
        orExpr.children.drop(1).forEach { child ->
            result = result.union(processLiteral(child))
        }
        return result
    }

    private fun processDnf(expr: Expression<String>): Set<Int> {
        return when (expr.exprType) {
            "variable" -> {
                processVariable(expr as Variable<String>)
            }
            "not" -> {
                processNot(expr as Not<String>)
            }
            "or" -> {
                processOr(expr as Or<String>)
            }
            "and" -> {
                processAnd(expr as And<String>)
            }
            else -> {
                throw QueryException("Can not process expression with type: '${expr.exprType}'")
            }
        }
    }
}
